package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FreshSabziRepository(private val dao: FreshSabziDao) {

    val allVegetables: Flow<List<Vegetable>> = dao.getAllVegetables()
    val allOrders: Flow<List<Order>> = dao.getAllOrders()
    val allUsers: Flow<List<User>> = dao.getAllUsers()
    val cartItems: Flow<List<CartItem>> = dao.getCartItems()

    suspend fun seedInitialVegetablesIfNeeded() {
        val currentVegs = allVegetables.first()
        if (currentVegs.isEmpty()) {
            val list = listOf(
                Vegetable(
                    name = "Spinach (Palak)",
                    nameHindi = "पालक",
                    category = "Leafy Vegetables",
                    pricePerKg = 40.0,
                    stockKg = 50.0,
                    isOrganic = false,
                    description = "Freshly plucked, iron-rich green spinach leaves. Perfect for Palak Paneer or soup.",
                    descriptionHindi = "ताजा तोड़े गए लौह समृद्ध हरे पालक के पत्ते। पालक पनीर या सूप के लिए उत्तम।",
                    imageType = "spinach",
                    discountPercent = 10
                ),
                Vegetable(
                    name = "Fresh Coriander (Dhaniya)",
                    nameHindi = "धनिया",
                    category = "Herbs",
                    pricePerKg = 60.0,
                    stockKg = 15.0,
                    isOrganic = false,
                    description = "Aromatic fresh green coriander leaves, perfect for garnishing and chutneys.",
                    descriptionHindi = "खुशबूदार हरी धनिया पत्ती, गार्निशिंग और चटनी के लिए आदर्श।",
                    imageType = "coriander",
                    discountPercent = 0
                ),
                Vegetable(
                    name = "Potato (Aloo)",
                    nameHindi = "आलू",
                    category = "Root Vegetables",
                    pricePerKg = 30.0,
                    stockKg = 200.0,
                    isOrganic = false,
                    description = "Premium quality, field-fresh potatoes. Essential for every Indian household kitchen.",
                    descriptionHindi = "उत्कृष्ट गुणवत्ता वाले, ताजे आलू। हर भारतीय रसोई के लिए आवश्यक।",
                    imageType = "potato",
                    discountPercent = 5
                ),
                Vegetable(
                    name = "Onion (Pyaz)",
                    nameHindi = "प्याज",
                    category = "Root Vegetables",
                    pricePerKg = 35.0,
                    stockKg = 180.0,
                    isOrganic = false,
                    description = "Crispy, multi-layered premium red onions. Add incredible flavour to curries and salads.",
                    descriptionHindi = "ताजा कड़क लाल प्याज। करी और सलाद में शानदार स्वाद जोड़ता है।",
                    imageType = "onion",
                    discountPercent = 15
                ),
                Vegetable(
                    name = "Tomato (Tamatar)",
                    nameHindi = "टमाटर",
                    category = "Fruits",
                    pricePerKg = 25.0,
                    stockKg = 120.0,
                    isOrganic = false,
                    description = "Tangy, juicy red tomatoes freshly delivered from local farms.",
                    descriptionHindi = "स्थानीय खेतों से सीधे लाए गए रसीले लाल टमाटर।",
                    imageType = "tomato",
                    discountPercent = 8
                ),
                Vegetable(
                    name = "Lady Finger (Bhindi)",
                    nameHindi = "भिंडी",
                    category = "Fruits",
                    pricePerKg = 50.0,
                    stockKg = 60.0,
                    isOrganic = false,
                    description = "Tender green Lady Finger / Okra. Best for preparing crispy Bhindi fry.",
                    descriptionHindi = "ताजी हरी भिंडी। कुरकुरी भिंडी भुजिया बनाने के लिए सर्वोत्तम।",
                    imageType = "bhindi",
                    discountPercent = 0
                ),
                Vegetable(
                    name = "Organic Broccoli",
                    nameHindi = "ऑर्गेनिक ब्रोकोली",
                    category = "Organic Vegetables",
                    pricePerKg = 150.0,
                    stockKg = 25.0,
                    isOrganic = true,
                    description = "Chemical-free, highly nutritious organic broccoli clusters imported from clean soils.",
                    descriptionHindi = "रसायन-मुक्त, स्वच्छ और अत्यधिक पौष्टिक ऑर्गेनिक ब्रोकली।",
                    imageType = "broccoli",
                    discountPercent = 12
                ),
                Vegetable(
                    name = "Organic Spinach (Palak)",
                    nameHindi = "ऑर्गेनिक पालक",
                    category = "Organic Vegetables",
                    pricePerKg = 65.0,
                    stockKg = 30.0,
                    isOrganic = true,
                    description = "100% Certified organic spinach, free of artificial pesticides and grown with pure compost.",
                    descriptionHindi = "कृत्रिम कीटनाशकों से मुक्त, शुद्ध खाद से उगाया गया १००% जैविक पालक।",
                    imageType = "organic_spinach",
                    discountPercent = 10
                ),
                Vegetable(
                    name = "Sweet Red Apple (Seb)",
                    nameHindi = "सेब",
                    category = "Fruits",
                    pricePerKg = 180.0,
                    stockKg = 70.0,
                    isOrganic = false,
                    description = "Crunchy and sweet Shimla apples, filled with healthy fiber and rich vitamins.",
                    descriptionHindi = "कुरकुरा और मीठा शिमला सेब, स्वस्थ फाइबर और विटामिन से भरपूर।",
                    imageType = "apple",
                    discountPercent = 10
                ),
                Vegetable(
                    name = "Carrot (Gajar)",
                    nameHindi = "गाजर",
                    category = "Root Vegetables",
                    pricePerKg = 40.0,
                    stockKg = 100.0,
                    isOrganic = false,
                    description = "Sweet and crisp orange carrots. Great for juices, salads, and sweet Gajar Halwa.",
                    descriptionHindi = "मीठी और कुरकुरी गाजर। जूस, सलाद और स्वादिष्ट गाजर का हलवा बनाने के लिए बेहतरीन।",
                    imageType = "carrot",
                    discountPercent = 0
                ),
                Vegetable(
                    name = "Green Chillies (Hari Mirch)",
                    nameHindi = "हरी मिर्च",
                    category = "Herbs",
                    pricePerKg = 70.0,
                    stockKg = 20.0,
                    isOrganic = false,
                    description = "Hot and spicy fresh green chillies, adding authentic Indian spice to your cooking.",
                    descriptionHindi = "तीखी और मसालेदार ताजी हरी मिर्च, आपके भोजन में प्रामाणिक तीखापन जोड़ती है।",
                    imageType = "chilli",
                    discountPercent = 5
                ),
                Vegetable(
                    name = "Fresh Mint (Pudina)",
                    nameHindi = "पुदीना",
                    category = "Herbs",
                    pricePerKg = 30.0,
                    stockKg = 15.0,
                    isOrganic = false,
                    description = "Refreshing and fragrant green mint, ideal for chutneys and summer coolers.",
                    descriptionHindi = "ताजा और सुगंधित हरा पुदीना, चटनी और गर्मियों के पेयों के लिए आदर्श।",
                    imageType = "mint",
                    discountPercent = 0
                )
            )
            for (veg in list) {
                dao.insertVegetable(veg)
            }
        }
        
        // Seed default accounts to let user switch quickly and test
        // Admin, Delivery Boy, and Customer (already set)
        if (dao.getUserByPhone("9876543210") == null) {
            dao.insertUser(User(
                phoneNumber = "9876543210",
                name = "Amit Kumar (Admin)",
                email = "admin@freshsabzi.com",
                role = "ADMIN",
                referralCode = "SABZI987",
                address = "Near Central Mall, MG Road, New Delhi",
                isGoogleUser = false
            ))
        }
        if (dao.getUserByPhone("8888888888") == null) {
            dao.insertUser(User(
                phoneNumber = "8888888888",
                name = "Ramesh Kumar (Delivery Driver)",
                email = "ramesh@freshsabzi.com",
                role = "DELIVERY_PARTNER",
                referralCode = "RAME888",
                address = "Delivery Hub, Sector 15, New Delhi",
                isGoogleUser = false
            ))
        }
    }

    suspend fun getUserByPhone(phone: String): User? = dao.getUserByPhone(phone)
    suspend fun insertUser(user: User) = dao.insertUser(user)
    suspend fun updateUser(user: User) = dao.updateUser(user)
    
    // Vegetables Management (Admin)
    suspend fun insertVegetable(vegetable: Vegetable) = dao.insertVegetable(vegetable)
    suspend fun updateVegetable(vegetable: Vegetable) = dao.updateVegetable(vegetable)
    suspend fun deleteVegetable(vegetable: Vegetable) = dao.deleteVegetable(vegetable)
    suspend fun getVegetableById(id: Int): Vegetable? = dao.getVegetableById(id)

    // Cart Management
    suspend fun addOrUpdateCartItem(vegId: Int, quantityKg: Double) {
        if (quantityKg <= 0) {
            dao.deleteCartItem(vegId)
        } else {
            dao.insertCartItem(CartItem(vegId, quantityKg))
        }
    }
    suspend fun deleteCartItem(vegId: Int) = dao.deleteCartItem(vegId)
    suspend fun clearCart() = dao.clearCart()

    // Orders Management
    suspend fun insertOrder(order: Order): Long = dao.insertOrder(order)
    suspend fun updateOrder(order: Order) = dao.updateOrder(order)
    suspend fun getOrderById(id: Int): Order? = dao.getOrderById(id)
    fun getOrdersByUser(userId: String) = dao.getOrdersByUser(userId)
    fun getOrdersByDeliveryPartner(partnerId: String) = dao.getOrdersByDeliveryPartner(partnerId)
}
