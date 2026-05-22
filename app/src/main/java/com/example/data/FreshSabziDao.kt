package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FreshSabziDao {
    
    // Vegetables
    @Query("SELECT * FROM vegetables ORDER BY name ASC")
    fun getAllVegetables(): Flow<List<Vegetable>>
    
    @Query("SELECT * FROM vegetables WHERE id = :id LIMIT 1")
    suspend fun getVegetableById(id: Int): Vegetable?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVegetable(vegetable: Vegetable)
    
    @Update
    suspend fun updateVegetable(vegetable: Vegetable)
    
    @Delete
    suspend fun deleteVegetable(vegetable: Vegetable)
    
    // Users
    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    
    // Orders
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY timestamp DESC")
    fun getOrdersByUser(userId: String): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE deliveryPartnerId = :partnerId ORDER BY timestamp DESC")
    fun getOrdersByDeliveryPartner(partnerId: String): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Int): Order?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long
    
    @Update
    suspend fun updateOrder(order: Order)
    
    // Cart
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)
    
    @Query("DELETE FROM cart_items WHERE vegetableId = :vegId")
    suspend fun deleteCartItem(vegId: Int)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
