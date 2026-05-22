package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FreshSabziViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FreshSabziRepository(database.dao())

    // Language state: "en" (English) or "hi" (Hindi)
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Dark Mode state
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Auth State: Current logged in User
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // All elements
    val vegetables: StateFlow<List<Vegetable>> = repository.allVegetables
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Simulated Active Notifications
    private val _notifications = MutableStateFlow<List<String>>(emptyList())
    val notifications: StateFlow<List<String>> = _notifications.asStateFlow()

    // Discount System
    private val _activeCoupon = MutableStateFlow<String?>(null)
    val activeCoupon: StateFlow<String?> = _activeCoupon.asStateFlow()

    // Coupon discount details: "FRESH20" is 20% off, "WELCOME50" is flat ₹50 off, "SABZI10" is 10% off
    val couponDiscountPercent = _activeCoupon.map { coupon ->
        when (coupon) {
            "FRESH20" -> 20
            "SABZI10" -> 10
            else -> 0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val couponFlatDiscount = _activeCoupon.map { coupon ->
        when (coupon) {
            "WELCOME50" -> 50.0
            else -> 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Derived: Cart items with full Vegetable details
    val cartWithDetails = combine(cartItems, vegetables) { cartList, vegList ->
        cartList.mapNotNull { cartItem ->
            val vegetable = vegList.find { it.id == cartItem.vegetableId }
            if (vegetable != null) {
                CartWithVegDetail(cartItem, vegetable)
            } else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weight & Price calculations
    val totalWeightKg = cartWithDetails.map { list ->
        list.sumOf { it.cartItem.quantityKg }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val subtotal = cartWithDetails.map { list ->
        list.sumOf { 
            val price = it.vegetable.pricePerKg * (1.0 - (it.vegetable.discountPercent / 100.0))
            price * it.cartItem.quantityKg 
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ₹20 delivery fee per kg
    val deliveryCharge = totalWeightKg.map { weight ->
        if (weight > 0) Math.ceil(weight) * 20.0 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val discountAmount = combine(subtotal, couponDiscountPercent, couponFlatDiscount) { sub, pct, flat ->
        val percentSavings = sub * (pct / 100.0)
        percentSavings + if (sub > flat) flat else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val grandTotal = combine(subtotal, deliveryCharge, discountAmount) { sub, delivery, discount ->
        val total = sub + delivery - discount
        if (total > 0) total else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        // Seed database and set default mock state or load user
        viewModelScope.launch {
            repository.seedInitialVegetablesIfNeeded()
        }
    }

    // Toggle language
    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == "en") "hi" else "en"
    }

    // Toggle dark mode
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Push local app notification
    fun showNotification(messageKeyEn: String, messageKeyHi: String) {
        val msg = if (_currentLanguage.value == "en") messageKeyEn else messageKeyHi
        val list = _notifications.value.toMutableList()
        list.add(0, msg)
        _notifications.value = list
    }

    // Clear notifications
    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    // Login or Signup
    fun loginOrSignup(name: String, phone: String, email: String, role: String, refCode: String, referredBy: String = "") {
        viewModelScope.launch {
            var user = repository.getUserByPhone(phone)
            if (user == null) {
                // Referral logic: New signup earns ₹50 if referred, referrer (if exists) earns ₹50
                var rewards = 0.0
                if (referredBy.isNotEmpty()) {
                    rewards = 50.0
                    // Credit referrer
                    val referrer = repository.getUserByPhone(referredBy)
                    if (referrer != null) {
                        referrer.referralPoints += 50.0
                        repository.updateUser(referrer)
                    }
                }
                user = User(
                    phoneNumber = phone,
                    name = name,
                    email = email,
                    role = role,
                    referralCode = "SABZI" + phone.takeLast(4),
                    referredBy = referredBy,
                    referralPoints = rewards,
                    address = "Sector 12, Janakpuri, New Delhi",
                    isGoogleUser = false
                )
                repository.insertUser(user)
                showNotification(
                    "Welcome $name! Referral bonus of ₹50 added.",
                    "स्वागत है $name! ₹50 का रेफरल बोनस जोड़ा गया।"
                )
            } else {
                // Update fields if switched or logged in
                user.name = name
                user.email = email
                user.role = role
                repository.updateUser(user)
                showNotification(
                    "Logged in successfully as ${user.name}!",
                    "${user.name} के रूप में सफलतापूर्वक लॉग इन किया गया!"
                )
            }
            _currentUser.value = user
        }
    }

    // Log out
    fun logout() {
        _currentUser.value = null
        showNotification("Logged out successfully.", "सफलतापूर्वक लॉग आउट हो गया।")
    }

    // Handle Promo Codes
    fun applyPromoCode(code: String): Boolean {
        return if (code == "FRESH20" || code == "WELCOME50" || code == "SABZI10") {
            _activeCoupon.value = code
            showNotification("Promo code $code applied!", "प्रोमो कोड $code लागू किया गया!")
            true
        } else {
            false
        }
    }

    fun removePromoCode() {
        val current = _activeCoupon.value
        if (current != null) {
            _activeCoupon.value = null
            showNotification("Promo code removed.", "प्रोमो कोड हटा दिया गया।")
        }
    }

    // Edit Profile details
    fun updateProfile(name: String, email: String, address: String, instructions: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            user.name = name
            user.email = email
            user.address = address
            user.deliveryInstructions = instructions
            repository.updateUser(user)
            _currentUser.value = user
            showNotification("Profile updated successfully!", "प्रोफ़ाइल सफलतापूर्वक अपडेट की गई!")
        }
    }

    // Cart Interactions
    fun updateCartQuantity(vegId: Int, change: Double) {
        viewModelScope.launch {
            val currentList = cartItems.value
            val currentItem = currentList.find { it.vegetableId == vegId }
            val currentQty = currentItem?.quantityKg ?: 0.0
            val newQty = (currentQty + change).coerceAtLeast(0.0)
            
            // Limit stock check
            val veg = repository.getVegetableById(vegId)
            if (veg != null && newQty > veg.stockKg) {
                showNotification(
                    "Cannot add! Only ${veg.stockKg} kg available in stock.",
                    "जोड़ नहीं सकते! स्टॉक में केवल ${veg.stockKg} किलोग्राम उपलब्ध है।"
                )
                return@launch
            }

            repository.addOrUpdateCartItem(vegId, newQty)
        }
    }

    fun removeCartItem(vegId: Int) {
        viewModelScope.launch {
            repository.deleteCartItem(vegId)
        }
    }

    // Order Placement (Checkout)
    fun placeOrder(paymentMethod: String, address: String, instructions: String, onSuccess: (Order) -> Unit) {
        val user = _currentUser.value ?: return
        val currentCartData = cartWithDetails.value
        if (currentCartData.isEmpty()) return

        viewModelScope.launch {
            // Build Serialized Cart items string formatted as: "id,name,qty,price,imageType||..."
            val serialized = currentCartData.joinToString("||") {
                val finalPrice = it.vegetable.pricePerKg * (1.0 - (it.vegetable.discountPercent / 100.0))
                "${it.vegetable.id},${it.vegetable.name},${it.cartItem.quantityKg},${finalPrice},${it.vegetable.imageType}"
            }

            val finalOrder = Order(
                userId = user.phoneNumber,
                userName = user.name,
                userPhone = user.phoneNumber,
                itemsData = serialized,
                totalWeightKg = totalWeightKg.value,
                subtotal = subtotal.value,
                deliveryCharge = deliveryCharge.value,
                discountAmount = discountAmount.value,
                grandTotal = grandTotal.value,
                paymentMethod = paymentMethod,
                paymentStatus = if (paymentMethod == "COD") "PENDING" else "PAID",
                shippingAddress = address,
                deliveryInstructions = instructions,
                status = "PLACED"
            )

            // Deduct vegetable stock
            for (cartRow in currentCartData) {
                val veg = cartRow.vegetable
                val updatedVeg = veg.copy(
                    stockKg = (veg.stockKg - cartRow.cartItem.quantityKg).coerceAtLeast(0.0)
                )
                repository.updateVegetable(updatedVeg)
            }

            val orderId = repository.insertOrder(finalOrder)
            val savedOrder = repository.getOrderById(orderId.toInt())
            if (savedOrder != null) {
                repository.clearCart()
                _activeCoupon.value = null // reset coupon
                showNotification(
                    "Order #${savedOrder.id} placed successfully using $paymentMethod!",
                    "ऑर्डर #${savedOrder.id} $paymentMethod का उपयोग करके सफलतापूर्वक स्वीकार किया गया!"
                )
                onSuccess(savedOrder)
            }
        }
    }

    // Submit review Rating & Comments
    fun rateOrder(orderId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                order.reviewRating = rating
                order.reviewComment = comment
                repository.updateOrder(order)
                showNotification(
                    "Thank you for rating Order #${order.id}!",
                    "ऑर्डर #${order.id} को रेट करने के लिए धन्यवाद!"
                )
            }
        }
    }

    // DELIVERY PARTNER ACTIONS
    fun deliveryAcceptOrder(orderId: Int, partnerId: String, partnerName: String) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                order.status = "ACCEPTED"
                order.deliveryPartnerId = partnerId
                order.deliveryPartnerName = partnerName
                repository.updateOrder(order)
                showNotification(
                    "Order #${order.id} accepted! Customer is on the way.",
                    "ऑर्डर #${order.id} स्वीकार कर लिया गया है! ग्राहक को सूचना भेज दी गई है।"
                )
            }
        }
    }

    fun deliveryUpdateStatus(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                order.status = newStatus
                if (newStatus == "DELIVERED" && order.paymentMethod == "COD") {
                    // Update order to PAID on delivery
                    // Let's create an updated instance
                }
                repository.updateOrder(order)
                showNotification(
                    "Order #${order.id} status updated to: $newStatus!",
                    "ऑर्डर #${order.id} का स्टेटस बदलकर किया गया: $newStatus!"
                )
            }
        }
    }

    // ADMIN ACTIONS
    fun adminAddOrUpdateVegetable(
        id: Int,
        name: String,
        nameHindi: String,
        category: String,
        price: Double,
        stock: Double,
        isOrganic: Boolean,
        desc: String,
        descHindi: String,
        imageType: String,
        discount: Int
    ) {
        viewModelScope.launch {
            if (id == 0) {
                // Add
                val veg = Vegetable(
                    name = name,
                    nameHindi = nameHindi,
                    category = category,
                    pricePerKg = price,
                    stockKg = stock,
                    isOrganic = isOrganic,
                    description = desc,
                    descriptionHindi = descHindi,
                    imageType = imageType,
                    discountPercent = discount
                )
                repository.insertVegetable(veg)
                showNotification("Added vegetable $name successfully!", "सब्जी $name सफलतापूर्वक जोड़ी गई!")
            } else {
                // Update
                val existing = repository.getVegetableById(id)
                if (existing != null) {
                    val updated = existing.copy(
                        name = name,
                        nameHindi = nameHindi,
                        category = category,
                        pricePerKg = price,
                        stockKg = stock,
                        isOrganic = isOrganic,
                        description = desc,
                        descriptionHindi = descHindi,
                        imageType = imageType,
                        discountPercent = discount
                    )
                    repository.updateVegetable(updated)
                    showNotification("Updated vegetable $name successfully!", "सब्जी $name सफलतापूर्वक संशोधित की गई!")
                }
            }
        }
    }

    fun adminDeleteVegetable(vegetable: Vegetable) {
        viewModelScope.launch {
            repository.deleteVegetable(vegetable)
            showNotification("Deleted ${vegetable.name} from inventory.", "${vegetable.name} को इन्वेंट्री से हटाया गया।")
        }
    }
}

// Model wrapper for Cart list UI
data class CartWithVegDetail(
    val cartItem: CartItem,
    val vegetable: Vegetable
)
