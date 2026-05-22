package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "vegetables")
data class Vegetable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nameHindi: String,
    val category: String, // Leafy Vegetables, Root Vegetables, Fruits, Herbs, Organic Vegetables
    val pricePerKg: Double,
    val stockKg: Double,
    val isOrganic: Boolean = false,
    val description: String = "",
    val descriptionHindi: String = "",
    val imageType: String = "vector", // "spinach", "potato", "carrot", "tomato", "onion", "apple", "coriander", "organic_spinach" etc.
    val discountPercent: Int = 0, // Daily Offers
    val rating: Double = 4.5,
    val totalReviews: Int = 12
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val phoneNumber: String, // Mobile number
    var name: String,
    var email: String = "",
    var role: String = "CUSTOMER", // CUSTOMER, DELIVERY_PARTNER, ADMIN
    val isGoogleUser: Boolean = false,
    var referralCode: String = "",
    var referredBy: String = "",
    var referralPoints: Double = 0.0,
    var address: String = "",
    var deliveryInstructions: String = ""
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val itemsData: String, // Custom structured: "vegetableId,name,qty,price,imageType||..."
    val totalWeightKg: Double,
    val subtotal: Double,
    val deliveryCharge: Double,
    val discountAmount: Double,
    val grandTotal: Double,
    val paymentMethod: String, // UPI, razorpay, Cash on Delivery (COD)
    val paymentStatus: String, // PENDING, PAID
    val shippingAddress: String,
    val deliveryInstructions: String = "",
    var status: String = "PLACED", // PLACED, ACCEPTED, PICKED, ON_THE_WAY, DELIVERED
    var deliveryPartnerId: String? = null,
    var deliveryPartnerName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    var reviewRating: Int? = null,
    var reviewComment: String? = null
) {
    // Helper to extract items
    fun getParsedItems(): List<ParsedOrderItem> {
        if (itemsData.isEmpty()) return emptyList()
        return itemsData.split("||").mapNotNull {
            val parts = it.split(",")
            if (parts.size >= 5) {
                ParsedOrderItem(
                    vegetableId = parts[0].toIntOrNull() ?: 0,
                    name = parts[1],
                    quantityKg = parts[2].toDoubleOrNull() ?: 0.0,
                    pricePerKg = parts[3].toDoubleOrNull() ?: 0.0,
                    imageType = parts[4]
                )
            } else null
        }
    }
}

data class ParsedOrderItem(
    val vegetableId: Int,
    val name: String,
    val quantityKg: Double,
    val pricePerKg: Double,
    val imageType: String
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val vegetableId: Int,
    val quantityKg: Double
)
