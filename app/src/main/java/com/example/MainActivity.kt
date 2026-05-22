@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.Order
import com.example.data.ParsedOrderItem
import com.example.data.User
import com.example.data.Vegetable
import com.example.ui.FreshSabziViewModel
import com.example.ui.VegetableGraphic
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: FreshSabziViewModel = viewModel()
            val isDark by appViewModel.isDarkMode.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDark) {
                FreshSabziApp(viewModel = appViewModel)
            }
        }
    }
}

@Composable
fun FreshSabziApp(viewModel: FreshSabziViewModel) {
    val navController = rememberNavController()
    val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

    // Multi-Language Strings Local Dictionary
    val textHome = if (isHi) "होम" else "Home"
    val textCart = if (isHi) "कार्ट" else "Cart"
    val textSettings = if (isHi) "प्रोफ़ाइल" else "Profile"
    val textDelivery = if (isHi) "वितरण" else "Delivery"
    val textAdmin = if (isHi) "एडमिन" else "Admin"

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        topBar = {
            if (currentRoute != "splash") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Quick Demo Switcher and Status Bar
                    DemoHeader(viewModel = viewModel, navController = navController)
                    
                    // Custom Gorgeous Vibrant Palette Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .drawBehind {
                                val strokeWidth = 1.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    color = if (isDark) Color(0xFF334155) else Color(0xFFE8F5E9), // Emerald 100/300 equivalent line
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Back Navigation Link
                            if (currentRoute != "home" && currentRoute != "login" && currentRoute != "delivery_dashboard" && currentRoute != "admin_dashboard") {
                                IconButton(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("nav_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Emerald Box Logo Icon F
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FreshGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "F",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            // Brand Display Title
                            Text(
                                text = "FreshSabzi",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp,
                                color = if (isDark) DarkGreenPrimary else Color(0xFF064E3B), // deepemerald
                                letterSpacing = (-0.5).sp
                            )
                        }

                        // Right header section: Address pill and quick switchers
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Address Pill: "Indiranagar"
                            val addressText = if (isHi) "इंदिरानगर" else "Indiranagar"
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFECFDF5))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = addressText.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                            }

                            // Dark / Light Toggle
                            IconButton(
                                onClick = { viewModel.toggleDarkMode() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Toggle Theme",
                                    modifier = Modifier.size(17.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            // Language Toggle
                            IconButton(
                                onClick = { viewModel.toggleLanguage() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("lang_toggle_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Change Language",
                                    modifier = Modifier.size(17.dp),
                                    tint = FreshGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentRoute != "splash" && currentRoute != "login" && !currentRoute.startsWith("order_tracking")) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                        } },
                        icon = { Icon(Icons.Default.Home, contentDescription = textHome) },
                        label = { Text(textHome, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_home_tab")
                    )

                    val cartItemsCount by viewModel.cartItems.collectAsStateWithLifecycle()
                    NavigationBarItem(
                        selected = currentRoute == "cart",
                        onClick = { navController.navigate("cart") },
                        icon = {
                            BadgedBox(badge = {
                                if (cartItemsCount.isNotEmpty()) {
                                    Badge { Text(cartItemsCount.size.toString()) }
                                }
                            }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = textCart)
                            }
                        },
                        label = { Text(textCart, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_cart_tab")
                    )

                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") },
                        icon = { Icon(Icons.Default.Person, contentDescription = textSettings) },
                        label = { Text(textSettings, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_profile_tab")
                    )

                    // Show Delivery Partner tab if user is DELIVERY_PARTNER
                    if (currentUser?.role == "DELIVERY_PARTNER") {
                        NavigationBarItem(
                            selected = currentRoute == "delivery_dashboard",
                            onClick = { navController.navigate("delivery_dashboard") },
                            icon = { Icon(Icons.Default.DeliveryDining, contentDescription = textDelivery) },
                            label = { Text(textDelivery, fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_delivery_tab")
                        )
                    }

                    // Show Admin tab if user is ADMIN
                    if (currentUser?.role == "ADMIN") {
                        NavigationBarItem(
                            selected = currentRoute == "admin_dashboard",
                            onClick = { navController.navigate("admin_dashboard") },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = textAdmin) },
                            label = { Text(textAdmin, fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_admin_tab")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("splash") {
                    SplashScreen(navController = navController, viewModel = viewModel)
                }
                composable("login") {
                    LoginScreen(navController = navController, viewModel = viewModel)
                }
                composable("home") {
                    HomeScreen(navController = navController, viewModel = viewModel)
                }
                composable(
                    route = "product_details/{vegId}",
                    arguments = listOf(navArgument("vegId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val vegId = backStackEntry.arguments?.getInt("vegId") ?: 0
                    ProductDetailsScreen(vegId = vegId, navController = navController, viewModel = viewModel)
                }
                composable("cart") {
                    CartScreen(navController = navController, viewModel = viewModel)
                }
                composable("checkout") {
                    CheckoutScreen(navController = navController, viewModel = viewModel)
                }
                composable(
                    route = "order_tracking/{orderId}",
                    arguments = listOf(navArgument("orderId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
                    OrderTrackingScreen(orderId = orderId, navController = navController, viewModel = viewModel)
                }
                composable("profile") {
                    ProfileScreen(navController = navController, viewModel = viewModel)
                }
                composable("delivery_dashboard") {
                    DeliveryDashboardScreen(navController = navController, viewModel = viewModel)
                }
                composable("admin_dashboard") {
                    AdminDashboardScreen(navController = navController, viewModel = viewModel)
                }
            }

            // In-app Push Notification Alerts Overlay
            val notificationsList by viewModel.notifications.collectAsStateWithLifecycle()
            if (notificationsList.isNotEmpty()) {
                NotificationToast(
                    message = notificationsList.first(),
                    onDismiss = { viewModel.clearNotifications() }
                )
            }
        }
    }
}

// SIMULATED OPTIONAL PERSPECTIVE QUICK-SWITCH badge helper
@Composable
fun DemoHeader(viewModel: FreshSabziViewModel, navController: NavHostController) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val userRoleText = when (currentUser?.role) {
            "ADMIN" -> if (isHi) "एडमिन मोड 🛠️" else "Admin Mode 🛠️"
            "DELIVERY_PARTNER" -> if (isHi) "डिलिवरी पार्टनर 🚴‍♂️" else "Delivery Partner 🚴‍♂️"
            else -> if (isHi) "ग्राहक मोड 🛒" else "Customer Mode 🛒"
        }
        Text(
            text = "${if (isHi) "सक्रिय भूमिका:" else "Active Role:"} $userRoleText",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Box {
            TextButton(
                onClick = { showMenu = true },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = if (isHi) "भूमिका बदलें ▾" else "Switch Persona ▾",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(if (isHi) "ग्राहक (सिम्युलेटेड उपयोगकर्ता)" else "Customer (Simulated User)") },
                    onClick = {
                        showMenu = false
                        viewModel.loginOrSignup(
                            name = "Sunil Sharma",
                            phone = "9898989898",
                            email = "sunil@gmail.com",
                            role = "CUSTOMER",
                            refCode = "SUN98"
                        )
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (isHi) "ड्राइवर रमेश (डिलिवरी)" else "Driver Ramesh (Delivery Partner)") },
                    onClick = {
                        showMenu = false
                        // Prepopulate ramesh
                        viewModel.loginOrSignup(
                            name = "Ramesh Kumar (Delivery Driver)",
                            phone = "8888888888",
                            email = "ramesh@freshsabzi.com",
                            role = "DELIVERY_PARTNER",
                            refCode = "RAME888"
                        )
                        scope.launch {
                            delay(100)
                            navController.navigate("delivery_dashboard")
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (isHi) "एडमिन अमित (प्रबंधक)" else "Admin Amit (Manager)") },
                    onClick = {
                        showMenu = false
                        viewModel.loginOrSignup(
                            name = "Amit Kumar (Admin)",
                            phone = "9876543210",
                            email = "admin@freshsabzi.com",
                            role = "ADMIN",
                            refCode = "SABZI987"
                        )
                        scope.launch {
                            delay(100)
                            navController.navigate("admin_dashboard")
                        }
                    }
                )
            }
        }
    }
}

// In-app simulated custom push notice banner
@Composable
fun BoxScope.NotificationToast(message: String, onDismiss: () -> Unit) {
    LaunchedEffect(key1 = message) {
        delay(4000)
        onDismiss()
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF333333),
            contentColor = Color.White
        ),
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(16.dp)
            .fillMaxWidth(0.9f)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .testTag("notification_toast"),
        onClick = onDismiss
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = "Alert",
                tint = Color(0xFFFFCC00),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ======================== PAGES ========================

// 1. SPLASH SCREEN
@Composable
fun SplashScreen(navController: NavController, viewModel: FreshSabziViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        delay(2500)
        if (currentUser == null) {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(Color.White, CircleShape)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "App Icon",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "FreshSabzi",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B5E20),
                letterSpacing = 1.sp
            )
            Text(
                text = "ताजा सब्जी, सीधे आपके घर!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            CircularProgressIndicator(
                color = Color(0xFF2E7D32),
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "Connecting Local Vendors...",
                fontSize = 12.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

// 2. LOGIN / REGISTER SCREEN
@Composable
fun LoginScreen(navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var referralCodeInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("CUSTOMER") } // CUSTOMER, DELIVERY_PARTNER, ADMIN

    var otpSent by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("login_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (otpSent) (if (isHi) "ओटीपी सत्यापित करें" else "Verify OTP") else (if (isHi) "लॉगिन / साइनअप" else "Login / Register"),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isHi) "ताजा सब्जी की तेज होम डिलीवरी" else "Get Fresh Vegetables Delivered Fast",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (!otpSent) {
                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isHi) "आपका नाम" else "Full Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Mobile Number
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10) phoneNumber = it },
                    label = { Text(if (isHi) "मोबाइल नंबर" else "Mobile Number") },
                    prefix = { Text("+91 ") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phone_input"),
                    leadingIcon = { Icon(Icons.Default.PhoneAndroid, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Referred By (Optional)
                OutlinedTextField(
                    value = referralCodeInput,
                    onValueChange = { referralCodeInput = it.uppercase() },
                    label = { Text(if (isHi) "रेफरल कोड (वैकल्पिक)" else "Referral Code (Optional)") },
                    placeholder = { Text("e.g. SABZI123") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Redeem, null) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Role Toggle Chip row
                Text(
                    text = if (isHi) "अपनी भूमिका चुनें:" else "Choose Your Role:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("CUSTOMER", "DELIVERY_PARTNER", "ADMIN").forEach { role ->
                        val label = when (role) {
                            "ADMIN" -> if (isHi) "एडमिन" else "Admin"
                            "DELIVERY_PARTNER" -> if (isHi) "वितरक" else "Delivery"
                            else -> if (isHi) "ग्राहक" else "Customer"
                        }
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Send OTP Button
                Button(
                    onClick = {
                        if (name.trim().isEmpty() || phoneNumber.length != 10) {
                            viewModel.showNotification(
                                "Please enter valid name and 10-digit mobile number.",
                                "कृपया सही नाम और 10 अंकों का मोबाइल नंबर दर्ज करें।"
                            )
                        } else {
                            isLoading = true
                            scope.launch {
                                delay(1200)
                                isLoading = false
                                otpSent = true
                                viewModel.showNotification(
                                    "Simulated SMS: Your FreshSabzi OTP is 1234.",
                                    "सिम्युलेटेड एसएमएस: आपका FreshSabzi ओटीपी 1234 है।"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("send_otp_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isHi) "ओटीपी भेजें ➔" else "Send OTP ➔", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // OTP VERIFICATION STEP
                Text(
                    text = if (isHi) "हमने आपके नंबर +91 $phoneNumber पर 4 अंकों का कोड भेजा है" else "We sent a 4-digit code to +91 $phoneNumber",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 4) otpCode = it },
                    label = { Text(if (isHi) "ओटीपी दर्ज करें" else "Enter OTP Code") },
                    placeholder = { Text("1234") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_input"),
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (otpCode == "1234" || otpCode == "") {
                            isLoading = true
                            scope.launch {
                                delay(1000)
                                isLoading = false
                                viewModel.loginOrSignup(
                                    name = name,
                                    phone = phoneNumber,
                                    email = "${name.lowercase().replace(" ", "")}@gmail.com",
                                    role = selectedRole,
                                    refCode = "SABZI" + phoneNumber.takeLast(4),
                                    referredBy = referralCodeInput
                                )
                                if (selectedRole == "DELIVERY_PARTNER") {
                                    navController.navigate("delivery_dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else if (selectedRole == "ADMIN") {
                                    navController.navigate("admin_dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        } else {
                            viewModel.showNotification("Incorrect OTP. Enter 1234.", "गलत ओटीपी। 1234 दर्ज करें।")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isHi) "सत्यापित करें और आगे बढ़ें" else "Verify & Proceed", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = { otpSent = false; otpCode = "" }) {
                    Text(if (isHi) "नंबर बदलें" else "Change Phone Number")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Simulated Google Login Button
            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        delay(1200)
                        isLoading = false
                        viewModel.loginOrSignup(
                            name = "Google User",
                            phone = "9111222333",
                            email = "user@gmail.com",
                            role = selectedRole,
                            refCode = "GOUG11"
                        )
                        if (selectedRole == "DELIVERY_PARTNER") {
                            navController.navigate("delivery_dashboard")
                        } else if (selectedRole == "ADMIN") {
                            navController.navigate("admin_dashboard")
                        } else {
                            navController.navigate("home")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("google_login_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GTranslate, // Placeholder for google icon
                        contentDescription = "Google",
                        tint = Color(0xFF4285F4),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(if (isHi) "गूगल के साथ लॉगिन करें" else "Sign In with Google", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// 3. HOME PAGE MARKETPLACE
@Composable
fun HomeScreen(navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    val vegetablesList by viewModel.vegetables.collectAsStateWithLifecycle()
    val cartItemsList by viewModel.cartItems.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Leafy Vegetables", "Root Vegetables", "Fruits", "Herbs", "Organic Vegetables")

    // Filter products
    val filteredList = vegetablesList.filter {
        val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || 
                            it.nameHindi.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    // Offers banner list
    val offerVegetables = vegetablesList.filter { it.discountPercent > 0 }

    Box(modifier = Modifier.fillMaxSize().testTag("home_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Search & Category Filter Section
            val isDark = isSystemInDarkTheme()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Search Bar in Pill Style
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            text = if (isHi) "ताजा आलू, टमाटर या पालक खोजें..." else "Search Potato, Tomato, Palak...",
                            fontSize = 13.sp,
                            color = Color.Gray
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("search_bar"),
                    leadingIcon = { 
                        Icon(
                            imageVector = Icons.Default.Search, 
                            contentDescription = null,
                            tint = FreshGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        ) 
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear, 
                                    contentDescription = "Clear",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(100.dp), // pill shape
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FreshGreenPrimary,
                        unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                        focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                        unfocusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
                    )
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Custom Emerald Categories Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val localizedName = when (cat) {
                            "All" -> if (isHi) "सभी" else "All"
                            "Leafy Vegetables" -> if (isHi) "पत्तेदार" else "Leafy Greens"
                            "Root Vegetables" -> if (isHi) "जड़ वाली" else "Roots"
                            "Fruits" -> if (isHi) "फल सब्जी" else "Fruits"
                            "Herbs" -> if (isHi) "धनिया / पुदीना" else "Herbs"
                            "Organic Vegetables" -> if (isHi) "ऑर्गेनिक 🌱" else "Organic 🌱"
                            else -> cat
                        }
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (isSelected) FreshGreenPrimary else (if (isDark) Color(0xFF1E293B) else Color(0xFFECFDF5))
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) FreshGreenPrimary else (if (isDark) Color(0xFF334155) else Color(0xFFD1FAE5)),
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = localizedName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else (if (isDark) Color(0xFF34D399) else Color(0xFF047857))
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcoming and Daily offers Banner Carousel
                if (offerVegetables.isNotEmpty() && searchQuery.isEmpty()) {
                    item {
                        Text(
                            text = if (isHi) "🔥 आज के विशेष ऑफर" else "🔥 Today's Special Offers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(offerVegetables) { veg ->
                                OfferBannerCard(veg = veg, isHi = isHi) {
                                    navController.navigate("product_details/${veg.id}")
                                }
                            }
                        }
                    }
                }

                // Inventory Listing Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHi) "सब्जी सूची (${filteredList.size})" else "Vegetable Marketplace (${filteredList.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                if (filteredList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = "Not found",
                                tint = Color.LightGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHi) "कोई सब्जी नहीं मिली!" else "No vegetables matched your search!",
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    item {
                        // Display items grid
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val chunked = filteredList.chunked(2)
                            chunked.forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    pair.forEach { veg ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            val cartItem = cartItemsList.find { it.vegetableId == veg.id }
                                            ProductGridCard(
                                                veg = veg,
                                                cartQty = cartItem?.quantityKg ?: 0.0,
                                                isHi = isHi,
                                                onSelect = { navController.navigate("product_details/${veg.id}") },
                                                onQtyChange = { change -> viewModel.updateCartQuantity(veg.id, change) }
                                            )
                                        }
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                // Extra referral info promotional banner
                item {
                    ReferralPromotionalBanner(currentUser = currentUser, isHi = isHi) {
                        navController.navigate("profile")
                    }
                }
            }
        }

        // Real-Time floating view Cart summary bar
        val cartWithDetails by viewModel.cartWithDetails.collectAsStateWithLifecycle()
        val totalWeight by viewModel.totalWeightKg.collectAsStateWithLifecycle()
        val grandTotal by viewModel.grandTotal.collectAsStateWithLifecycle()

        if (cartWithDetails.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(16.dp))
                    .testTag("floating_cart_banner"),
                onClick = { navController.navigate("cart") }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Emerald cart icon container
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FreshGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        Column {
                            Text(
                                text = if (isHi) "${cartWithDetails.size} सब्जियां | ${"%.2f".format(totalWeight)} KG" else "${cartWithDetails.size} Item${if(cartWithDetails.size > 1) "s" else ""} • ${"%.2f".format(totalWeight)} KG",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isHi) "वितरण शुल्क: ₹${"%.0f".format((totalWeight * 20.0).coerceAtLeast(30.0))} (₹20 / किलो)" else "Delivery: ₹${"%.0f".format((totalWeight * 20.0).coerceAtLeast(30.0))} (₹20/kg)",
                                fontSize = 10.sp,
                                color = Color(0xFF34D399), // text-emerald-400
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "₹${"%.1f".format(grandTotal)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = if (isHi) "चेकआउट करें" else "Checkout",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399), // text-emerald-400
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Checkout",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 4. PRODUCT DETAILS PAGE
@Composable
fun ProductDetailsScreen(vegId: Int, navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    val vegetablesList by viewModel.vegetables.collectAsStateWithLifecycle()
    val cartItemsList by viewModel.cartItems.collectAsStateWithLifecycle()

    val veg = vegetablesList.find { it.id == vegId }
    val cartItem = cartItemsList.find { it.vegetableId == vegId }
    val cartQty = cartItem?.quantityKg ?: 0.0

    if (veg == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found!")
        }
        return
    }

    val finalPrice = veg.pricePerKg * (1.0 - (veg.discountPercent / 100.0))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("product_details_screen")
    ) {
        // Vegetable Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            VegetableGraphic(type = veg.imageType, modifier = Modifier.size(140.dp))
            
            if (veg.isOrganic) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("100% ORGANIC", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            if (veg.discountPercent > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color(0xFFFF9100), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${veg.discountPercent}% OFF", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Titles and Pricing
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isHi) veg.nameHindi else veg.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = veg.category,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${veg.rating} (${veg.totalReviews})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Price tags
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "₹${"%.1f".format(finalPrice)}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "/ KG",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            if (veg.discountPercent > 0) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "M.R.P: ₹${veg.pricePerKg}",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Description panel
        Text(
            text = if (isHi) "विवरण" else "Description",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isHi) veg.descriptionHindi else veg.description,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            lineHeight = 20.sp
        )

        // Availability Stock Tag
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isHi) "उपलब्ध स्टॉक: ${veg.stockKg} किलोग्राम" else "Available Stock: ${veg.stockKg} KG",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (veg.stockKg > 10) Color(0xFF2E7D32) else Color.Red
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Cart Actions (Add to Cart / Increase & Decrease)
        Text(
            text = if (isHi) "मात्रा चुनें" else "Select Quantity",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (cartQty <= 0) {
            Button(
                onClick = { viewModel.updateCartQuantity(veg.id, 1.0) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("add_to_cart_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddShoppingCart, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isHi) "कार्ट में जोड़ें (1.0 KG)" else "Add to Cart (1.0 KG)", fontWeight = FontWeight.Bold)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Large Weight selectors
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(6.dp)
                ) {
                    listOf(-1.0, -0.5, 0.5, 1.0).forEach { amt ->
                        IconButton(
                            onClick = { viewModel.updateCartQuantity(veg.id, amt) },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Text(
                                text = if (amt > 0) "+${if(amt==0.5) "½" else "1"}" else "${if(amt==-0.5) "-½" else "-1"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Current Qty label
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${"%.1f".format(cartQty)} KG",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Subtotal: ₹${"%.1f".format(cartQty * finalPrice)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Return button
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isHi) "खरीदारी जारी रखें" else "Continue Shopping")
        }
    }
}

// Offer Banner component (Horizontal scroll row)
@Composable
fun OfferBannerCard(veg: Vegetable, isHi: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp), // rounded-3xl in Tailwind
        modifier = Modifier
            .width(280.dp)
            .height(115.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF10B981), // Emerald 500
                            Color(0xFF84CC16)  // Lime 500
                        )
                    )
                )
                .drawBehind {
                    // Draw abstract background semi-circles to match design (absolute -right-4 -bottom-4 w-24 h-24 bg-white/10 rounded-full)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.12f),
                        radius = 60.dp.toPx(),
                        center = Offset(size.width - 20.dp.toPx(), size.height - 10.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = 110.dp.toPx(),
                        center = Offset(size.width - 10.dp.toPx(), size.height)
                    )
                }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHi) "दैनिक विशेष ऑफर" else "DAILY OFFER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${veg.discountPercent}% OFF",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = if (isHi) "पर ${veg.nameHindi}" else "on ${veg.name}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "₹${"%.1f".format(veg.pricePerKg * (1.0 - (veg.discountPercent / 100.0)))}/KG",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.LineThrough.takeIf { false } // clean styling info
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Blurred coupon-style badge (bg-white/20, border-white/30)
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CODE: SABZI${veg.discountPercent}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    
                    VegetableGraphic(type = veg.imageType, modifier = Modifier.size(52.dp))
                }
            }
        }
    }
}

// Vegetable Card Grid component
@Composable
fun ProductGridCard(
    veg: Vegetable,
    cartQty: Double,
    isHi: Boolean,
    onSelect: () -> Unit,
    onQtyChange: (Double) -> Unit
) {
    val finalPrice = veg.pricePerKg * (1.0 - (veg.discountPercent / 100.0))
    val isDark = isSystemInDarkTheme()
    
    Card(
        shape = RoundedCornerShape(24.dp), // matched to Tailwind rounded-3xl
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isDark) 0.dp else 4.dp, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9), // border-slate-100/700
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onSelect)
            .testTag("veg_card_${veg.id}")
    ) {
        Column {
            // Header Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAF5)) // Soft light background tint
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                VegetableGraphic(type = veg.imageType, modifier = Modifier.size(68.dp))
                
                if (veg.isOrganic) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(Color(0xFFDCFCE7), RoundedCornerShape(100.dp)) // soft emerald accent
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = "Organic",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = if (isHi) "ऑर्गेनिक" else "Organic",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }

                if (veg.discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color(0xFFF97316), RoundedCornerShape(100.dp)) // orange-500
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${veg.discountPercent}% OFF",
                            fontSize = 8.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Text Info Box
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isHi) veg.nameHindi else veg.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = if (isDark) Color.White else Color(0xFF0F172A), // Slate900
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "₹${"%.1f".format(finalPrice)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (isDark) DarkGreenPrimary else Color(0xFF047857) // text-emerald-700
                    )
                    Text(
                        text = if (isHi) "/किलो" else "/kg",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (cartQty <= 0) {
                    Button(
                        onClick = { onQtyChange(0.5) },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FreshGreenPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .testTag("add_btn_${veg.id}"),
                        shape = RoundedCornerShape(12.dp) // rounded-xl active state
                    ) {
                        Text(
                            text = if (isHi) "जोड़ें +" else "Add +",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onQtyChange(-0.5) },
                            modifier = Modifier
                                .size(28.dp)
                                .background(FreshGreenPrimary, RoundedCornerShape(8.dp))
                        ) {
                            Text("-", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                        
                        Text(
                            text = "${"%.1f".format(cartQty)} KG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                        
                        IconButton(
                            onClick = { onQtyChange(0.5) },
                            modifier = Modifier
                                .size(28.dp)
                                .background(FreshGreenPrimary, RoundedCornerShape(8.dp))
                        ) {
                            Text("+", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralPromotionalBanner(currentUser: User?, isHi: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isHi) "कमाएं ₹50 उपहार!" else "Earn ₹50 For Every Referral!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isHi) "अपने दोस्तों को आमंत्रित करें और मुफ़्त सब्जियां पाएं!" else "Invite friends with code: ${currentUser?.referralCode ?: "SABZI"}!",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

// 5. SHOPPING CART PAGE
@Composable
fun CartScreen(navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    val cartWithDetails by viewModel.cartWithDetails.collectAsStateWithLifecycle()
    val totalWeight by viewModel.totalWeightKg.collectAsStateWithLifecycle()
    val subtotal by viewModel.subtotal.collectAsStateWithLifecycle()
    val deliveryCharge by viewModel.deliveryCharge.collectAsStateWithLifecycle()
    val discountAmount by viewModel.discountAmount.collectAsStateWithLifecycle()
    val grandTotal by viewModel.grandTotal.collectAsStateWithLifecycle()
    val activeCoupon by viewModel.activeCoupon.collectAsStateWithLifecycle()

    var couponField by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cart_screen")
    ) {
        Text(
            text = if (isHi) "आपकी टोकरी 🛒" else "My Basket 🛒",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (cartWithDetails.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.RemoveShoppingCart, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                Spacer(modifier = Modifier.height(10.dp))
                Text(if (isHi) "आपकी कार्ट खाली है!" else "Your cart is empty!", fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("home") }) {
                    Text(if (isHi) "सब्जी खरीदें" else "Shop Fresh Vegetables")
                }
            }
            return
        }

        // Cart items vertical list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cartWithDetails) { row ->
                val veg = row.vegetable
                val finalPrice = veg.pricePerKg * (1.0 - (veg.discountPercent / 100.0))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VegetableGraphic(type = veg.imageType, modifier = Modifier.size(50.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHi) veg.nameHindi else veg.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "₹${"%.1f".format(finalPrice)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Text(text = " / kg", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        // Add / Subweight controller of precisely 0.5kg increments
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.updateCartQuantity(veg.id, -0.5) }) {
                                Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = "${"%.1f".format(row.cartItem.quantityKg)} kg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(onClick = { viewModel.updateCartQuantity(veg.id, 0.5) }) {
                                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                        }

                        IconButton(onClick = { viewModel.removeCartItem(veg.id) }) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // Coupon Promo applicator card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isHi) "कूपन कोड लागू करें 🎟️" else "Apply Promo Code 🎟️",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = couponField,
                                onValueChange = { couponField = it.uppercase() },
                                modifier = Modifier.weight(1f),
                                label = { Text(if (isHi) "कोड" else "Enter Code") },
                                singleLine = true,
                                trailingIcon = {
                                    if (activeCoupon != null) {
                                        IconButton(onClick = { viewModel.removePromoCode(); couponField = "" }) {
                                            Icon(Icons.Default.Close, null)
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (viewModel.applyPromoCode(couponField)) {
                                        // succeeded
                                    } else {
                                        viewModel.showNotification("Invalid Promo Code (try FRESH20 or WELCOME50)", "अमान्य प्रोमो कोड (FRESH20 या WELCOME50 आज़माएं)")
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isHi) "लागू करें" else "Apply")
                            }
                        }
                        // Offers chips
                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(
                                onClick = { couponField = "FRESH20"; viewModel.applyPromoCode("FRESH20") },
                                label = { Text("FRESH20 (20% off)") }
                            )
                            AssistChip(
                                onClick = { couponField = "WELCOME50"; viewModel.applyPromoCode("WELCOME50") },
                                label = { Text("WELCOME50 (₹50 off)") }
                            )
                        }
                    }
                }
            }

            // Indian receipt breakups
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = if (isHi) "बिल विवरण" else "Bill Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ReceiptRow(label = if (isHi) "कुल वजन" else "Total Weight", value = "${"%.2f".format(totalWeight)} KG")
                        ReceiptRow(label = if (isHi) "आइटम वैल्यू" else "Items Price Subtotal", value = "₹${"%.1f".format(subtotal)}")
                        ReceiptRow(
                            label = if (isHi) "डिलिवरी शुल्क (₹20 × वजन)" else "Delivery Fee (₹20 per KG)", 
                            value = "₹${"%.1f".format(deliveryCharge)}",
                            subText = if (isHi) "आटोमेटिक वजन गणना" else "Automatic weight billing"
                        )
                        if (discountAmount > 0) {
                            ReceiptRow(
                                label = if (isHi) "बचत / डिस्काउंट 🏷️" else "Savings / Coupon Code 🏷️", 
                                value = "- ₹${"%.1f".format(discountAmount)}",
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 10.dp))
                        ReceiptRow(
                            label = if (isHi) "कुल देय राशि" else "Grand Payable Total", 
                            value = "₹${"%.1f".format(grandTotal)}", 
                            fontWeight = FontWeight.Black, 
                            fontSize = 17,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Proceed buttons
        Button(
            onClick = { navController.navigate("checkout") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp)
                .testTag("checkout_proceed_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isHi) "चेकआउट के लिए आगे बढ़ें ➔" else "Proceed to Checkout ➔", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ReceiptRow(
    label: String, 
    value: String, 
    fontWeight: FontWeight = FontWeight.Normal, 
    fontSize: Int = 13,
    color: Color = Color.Unspecified,
    subText: String? = null
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = fontSize.sp, fontWeight = fontWeight)
            Text(value, fontSize = fontSize.sp, fontWeight = fontWeight, color = color)
        }
        if (subText != null) {
            Text(subText, fontSize = 9.sp, color = Color.Gray)
        }
    }
}

// 6. CHECKOUT PAGE
@Composable
fun CheckoutScreen(navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val totalWeight by viewModel.totalWeightKg.collectAsStateWithLifecycle()
    val subtotal by viewModel.subtotal.collectAsStateWithLifecycle()
    val deliveryCharge by viewModel.deliveryCharge.collectAsStateWithLifecycle()
    val discountAmount by viewModel.discountAmount.collectAsStateWithLifecycle()
    val grandTotal by viewModel.grandTotal.collectAsStateWithLifecycle()

    var shippingAddress by remember { mutableStateOf(currentUser?.address ?: "Sector 10, Rohini, New Delhi") }
    var deliveryInstructions by remember { mutableStateOf(currentUser?.deliveryInstructions ?: "Leave at gate") }
    var paymentMethod by remember { mutableStateOf("COD") } // COD, RAZORPAY, UPI

    var isPayingSimulated by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().testTag("checkout_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (isHi) "चेकआउट और भुगतान 💳" else "Checkout & Delivery details 💳",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Address module
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(if (isHi) "📍 वितरण पता (Delivery Address)" else "📍 Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = shippingAddress,
                        onValueChange = { shippingAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = deliveryInstructions,
                        onValueChange = { deliveryInstructions = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(if (isHi) "उदा. गेट पर छोड़ दें या आने से पहले कॉल करें" else "e.g. Leave at gate, ring bell") },
                        label = { Text(if (isHi) "डिलिवरी निर्देश" else "Delivery Instructions") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Payment Options
            Text(if (isHi) "भुगतान विकल्प चुनें:" else "Choose Payment Method:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { paymentMethod = "COD" }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = paymentMethod == "COD", onClick = { paymentMethod = "COD" })
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.CurrencyRupee, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(if (isHi) "कैश ऑन डिलीवरी (COD)" else "Cash on Delivery (COD)", fontWeight = FontWeight.SemiBold)
                            Text(if (isHi) "सब्जी मिलने पर नकद या यूपीआई से भुगतान करें" else "Pay in cash/UPI when veggies arrive", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Divider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { paymentMethod = "UPI" }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = paymentMethod == "UPI", onClick = { paymentMethod = "UPI" })
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.QrCode, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("BHIM UPI (Instant Transfer)", fontWeight = FontWeight.SemiBold)
                            Text(if (isHi) "पेटीएम, गूगलपे या फोनपे द्वारा तीव्र सुरक्षित भुगतान" else "Pay instantly from GPay, PhonePe, Paytm", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Divider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { paymentMethod = "RAZORPAY" }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = paymentMethod == "RAZORPAY", onClick = { paymentMethod = "RAZORPAY" })
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.CreditCard, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Razorpay Online Gateway (Debit/Credit Card)", fontWeight = FontWeight.SemiBold)
                            Text(if (isHi) "सुरक्षित गेटवे - क्रेडिट कार्ड या नेट बैंकिंग" else "Securely pay using cards/wallets/Netbanking", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Bill checkout widget summary
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isHi) "देय राशि (Payable Amount):" else "Payable Amount:")
                    Text("₹${"%.1f".format(grandTotal)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ultimate Place Order Button
            Button(
                onClick = {
                    isPayingSimulated = true
                    scope.launch {
                        delay(2000) // simulated secured payment latency
                        isPayingSimulated = false
                        viewModel.placeOrder(
                            paymentMethod = paymentMethod,
                            address = shippingAddress,
                            instructions = deliveryInstructions,
                            onSuccess = { order ->
                                navController.navigate("order_tracking/${order.id}") {
                                    popUpTo("home")
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("place_order_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isHi) "ऑर्डर सबमिट करें - ₹${"%.1f".format(grandTotal)}" else "Place Order - ₹${"%.1f".format(grandTotal)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Razorpay secure payment gateway simulated overlay loader
        if (isPayingSimulated) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Razorpay",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color(0xFF0F52BA)
                        )
                        Text("Simulating Secure India payment node...", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(20.dp))
                        CircularProgressIndicator(color = Color(0xFF0F52BA))
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Please do not close or click back...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// 7. ORDER TRACKING & REVIEWS PAGE
@Composable
fun OrderTrackingScreen(orderId: Int, navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    val ordersList by viewModel.allOrders.collectAsStateWithLifecycle()
    val order = ordersList.find { it.id == orderId }

    var userRating by remember { mutableIntStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order #$orderId not found!")
        }
        return
    }

    // Determine current index on stepper
    val statuses = listOf("PLACED", "ACCEPTED", "PICKED", "ON_THE_WAY", "DELIVERED")
    val currentIndex = statuses.indexOf(order.status).coerceAtLeast(0)

    val listItems = order.getParsedItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("order_tracking_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHi) "ऑर्डर ट्रैकिंग #${order.id}" else "Track Order #${order.id}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = order.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Visual delivery progress stepper status tracking
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                statuses.forEachIndexed { idx, stat ->
                    val isDone = idx <= currentIndex
                    val activeColor = MaterialTheme.colorScheme.primary
                    val inactiveColor = Color.LightGray

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(if (isDone) activeColor else inactiveColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            } else {
                                Text((idx + 1).toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = when (stat) {
                                "PLACED" -> if (isHi) "ऑर्डर प्राप्त हुआ 🛒" else "Order Submitted 🛒"
                                "ACCEPTED" -> if (isHi) "विक्रेता द्वारा स्वीकृत 👍" else "Accepted by Seller 👍"
                                "PICKED" -> if (isHi) "पैकिंग पूरी हुई 🎒" else "Picked & Packed 🎒"
                                "ON_THE_WAY" -> if (isHi) "डिलीवरी रास्ते में है 🚴‍♂️" else "Out on Delivery 🚴‍♂️"
                                "DELIVERED" -> if (isHi) "सफलतापूर्वक वितरित ✅" else "Delivered Successfully ✅"
                                else -> stat
                            },
                            fontSize = 13.sp,
                            fontWeight = if (idx == currentIndex) FontWeight.Bold else FontWeight.Normal,
                            color = if (idx == currentIndex) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    if (idx < statuses.size - 1) {
                        Spacer(modifier = Modifier.width(1.dp).height(10.dp).background(if (idx < currentIndex) activeColor else inactiveColor).align(Alignment.Start).padding(start = 12.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Delivery Boy details & custom Whatsapp Support Button
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(if (isHi) "🚴‍♂️ वितरण सहायक (Delivery Executive)" else "🚴‍♂️ Delivery Executive", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                if (order.deliveryPartnerId != null) {
                    Text(text = "Name: ${order.deliveryPartnerName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(text = "Phone: +91 888888888", fontSize = 12.sp, color = Color.Gray)
                } else {
                    Text(if (isHi) "असाइन किया जा रहा है...." else "Assigning nearby rider....", fontSize = 13.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(10.dp))

                // WhatsApp Support Button
                val context = LocalContext.current
                Button(
                    onClick = {
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=919876543210&text=Hi, need support for FreshSabzi Order #${order.id}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not installed. Live Agent support: 9876543210", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.SupportAgent, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isHi) "व्हाट्सएप सहायता (WhatsApp Support)" else "WhatsApp Support Button", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Order Reciept Checklist items
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(if (isHi) "आइटम की सूची" else "Item Checklist", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                listItems.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.name} (${item.quantityKg} kg)", fontSize = 12.sp)
                        Text("₹${"%.1f".format(item.quantityKg * item.pricePerKg)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 6.dp))
                ReceiptRow(label = if (isHi) "आइटम सबटोटल" else "Subtotal", value = "₹${"%.1f".format(order.subtotal)}")
                ReceiptRow(label = if (isHi) "वितरण चार्ज" else "Delivery Charges", value = "₹${"%.1f".format(order.deliveryCharge)}")
                if (order.discountAmount > 0) {
                    ReceiptRow(label = if (isHi) "छूट" else "Discounts", value = "- ₹${"%.1f".format(order.discountAmount)}", color = Color(0xFF2E7D32))
                }
                ReceiptRow(label = if (isHi) "कुल राशि" else "Grand Total", value = "₹${"%.1f".format(order.grandTotal)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        // Ratings & reviews system (Only unlocked when status is DELIVERED)
        if (order.status == "DELIVERED") {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ratings_panel"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isHi) "⭐️ रेटिंग और समीक्षा (Ratings & Reviews)" else "⭐️ Ratings & Reviews feedback",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (isHi) "कैसी थी डिलीवरी और सब्जियों की गुणवत्ता?" else "How was the delivery and vegetable freshness?",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (order.reviewRating != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (isHi) "आपकी रेटिंग: " else "Your Rating: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            repeat(order.reviewRating ?: 0) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                            }
                        }
                        if (!order.reviewComment.isNullOrEmpty()) {
                            Text(
                                text = "\"${order.reviewComment}\"",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    } else {
                        // Rating Interactive stars selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..5).forEach { star ->
                                IconButton(onClick = { userRating = star }) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= userRating) Color(0xFFFFCC00) else Color.LightGray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            placeholder = { Text(if (isHi) "सब्जी बहुत ताजी थी, धन्यवाद!" else "Veggies were very fresh, thanks!") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { viewModel.rateOrder(order.id, userRating, reviewComment) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isHi) "रेटिंग सबमिट करें" else "Submit Feedback")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isHi) "बाजार वापस जाएं" else "Back to Marketplace")
        }
    }
}

// 8. PROFILE SCREEN
@Composable
fun ProfileScreen(navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val ordersList by viewModel.allOrders.collectAsStateWithLifecycle()
    val userOrders = ordersList.filter { it.userId == currentUser?.phoneNumber }

    var editingName by remember { mutableStateOf(currentUser?.name ?: "") }
    var editingEmail by remember { mutableStateOf(currentUser?.email ?: "") }
    var editingAddress by remember { mutableStateOf(currentUser?.address ?: "") }
    var editingInstructions by remember { mutableStateOf(currentUser?.deliveryInstructions ?: "") }

    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(currentUser?.name ?: "User", fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text(currentUser?.phoneNumber ?: "+91 9999999999", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        currentUser?.role ?: "CUSTOMER",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Referral Card rewards display
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(if (isHi) "🎁 रेफरल और पुरस्कार (Rewards)" else "🎁 Referral Rewards & Code", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(if (isHi) "आपका रेफरल कोड" else "Your Invite Code", fontSize = 11.sp, color = Color.Gray)
                        Text(currentUser?.referralCode ?: "SABZI1", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(if (isHi) "कुल जीत बोनस" else "Referral Wallet", fontSize = 11.sp, color = Color.Gray)
                        Text("₹${"%.1f".format(currentUser?.referralPoints ?: 0.0)}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Text(
                    text = if (isHi) "*नए उपयोगकर्ताओं द्वारा कूपन कोड दर्ज करने पर ₹50 वॉलेट बोनस" else "*Earn flat ₹50 cash points when contacts sign up using your code.",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Edit profile details
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isHi) "व्यक्तिगत विवरण" else "Personal & Address Info", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(if (isEditing) Icons.Default.Save else Icons.Default.Edit, "Edit")
                    }
                }

                if (isEditing) {
                    OutlinedTextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (isHi) "नाम" else "Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editingEmail,
                        onValueChange = { editingEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editingAddress,
                        onValueChange = { editingAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (isHi) "वितरण पता" else "Address") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editingInstructions,
                        onValueChange = { editingInstructions = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (isHi) "डिलिवरी निर्देश" else "Instructions") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.updateProfile(editingName, editingEmail, editingAddress, editingInstructions)
                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isHi) "विवरण सहेजें" else "Save Changes")
                    }
                } else {
                    Text(text = "Email: ${currentUser?.email?.ifEmpty { "None" }}", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Address: ${currentUser?.address?.ifEmpty { "No address configured" }}", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Instructions: ${currentUser?.deliveryInstructions?.ifEmpty { "None" }}", fontSize = 13.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Orders history
        Text(
            text = if (isHi) "आपका ऑर्डर इतिहास (${userOrders.size})" else "Order History Log (${userOrders.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (userOrders.isEmpty()) {
            Text(
                if (isHi) "कोई पूर्व आर्डर नहीं।" else "No orders placed yet.",
                fontSize = 13.sp,
                color = Color.Gray
            )
        } else {
            userOrders.forEach { ord ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { navController.navigate("order_tracking/${ord.id}") }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Order #${ord.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Paid: ₹${"%.1f".format(ord.grandTotal)}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = ord.status, fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { viewModel.logout(); navController.navigate("login") { popUpTo("home") { inclusive = true } } },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.ExitToApp, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isHi) "लॉग आउट करें" else "Logout")
        }
    }
}

// 9. DELIVERY PARTNER PANEL & DASHBOARD
@Composable
fun DeliveryDashboardScreen(navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val ordersList by viewModel.allOrders.collectAsStateWithLifecycle()

    val pendingPool = ordersList.filter { it.status == "PLACED" }
    val assignedActive = ordersList.filter { 
        it.deliveryPartnerId == currentUser?.phoneNumber && it.status != "DELIVERED" 
    }
    val driverDelivered = ordersList.filter { 
        it.deliveryPartnerId == currentUser?.phoneNumber && it.status == "DELIVERED" 
    }

    // Driver Earnings tracking at ₹15 flat per delivery payout
    val flatPayoutRule = 25.0
    val totalEarnings = driverDelivered.size * flatPayoutRule

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("delivery_screen")
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isHi) "चालक डैशबोर्ड (Rider Ramesh Dashboard)" else "Rider Ramesh Delivery Panel",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(text = currentUser?.name ?: "Delivery driver", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (isHi) "कुल वितरण" else "Deliveries", fontSize = 11.sp, color = Color.Gray)
                        Text("${driverDelivered.size}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (isHi) "कुल कमाई 💰" else "Total Earnings 💰", fontSize = 11.sp, color = Color.Gray)
                        Text("₹${"%.1f".format(totalEarnings)}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // ASSIGNED ACTIVE DRIVER ORDERS
        Text(
            text = if (isHi) "आपके सक्रिय आर्डर (${assignedActive.size})" else "Active Assigned Tasks (${assignedActive.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary
        )

        if (assignedActive.isEmpty()) {
            Text(
                if (isHi) "कोई सक्रिय ऑर्डर नहीं है। नीचे पुल से आर्डर स्वीकार करें।" else "No active duties. Pick orders from community pool below.",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            assignedActive.forEach { ord ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Order #${ord.id}", fontWeight = FontWeight.Bold)
                            Text(text = "Amount: ₹${ord.grandTotal}", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Customer: ${ord.userName} (${ord.userPhone})", fontSize = 12.sp)
                        Text(text = "Destination: ${ord.shippingAddress}", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "Weight: ${ord.totalWeightKg} KG", fontSize = 12.sp, color = Color.Gray)
                        
                        // Status Action controllers
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (ord.status) {
                                "ACCEPTED" -> {
                                    Button(
                                        onClick = { viewModel.deliveryUpdateStatus(ord.id, "PICKED") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (isHi) "पैक्ड / उठा लिया गया" else "Mark Picked Up")
                                    }
                                }
                                "PICKED" -> {
                                    Button(
                                        onClick = { viewModel.deliveryUpdateStatus(ord.id, "ON_THE_WAY") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (isHi) "रास्ते में चिह्नित करें" else "Mark Out for Delivery")
                                    }
                                }
                                "ON_THE_WAY" -> {
                                    Button(
                                        onClick = { viewModel.deliveryUpdateStatus(ord.id, "DELIVERED") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                    ) {
                                        Text(if (isHi) "वितरित चिह्नित करें ✅" else "Mark DELIVERED ✅")
                                    }
                                }
                            }
                            OutlinedButton(
                                onClick = { navController.navigate("order_tracking/${ord.id}") }
                            ) {
                                Text(if (isHi) "ट्रैक" else "Track")
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // PENDING ORDER community POOL
        Text(
            text = if (isHi) "अनुपलब्ध लंबित आर्डर पूल (${pendingPool.size})" else "Unassigned Vegetable Delivery Orders Pool (${pendingPool.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        if (pendingPool.isEmpty()) {
            Text(
                if (isHi) "कोई लंबित आर्डर नहीं हैं।" else "No unassigned customer tasks right now. Check back in a minute!",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            pendingPool.forEach { ord ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Order #${ord.id}", fontWeight = FontWeight.Bold)
                            Text(text = "Weight: ${ord.totalWeightKg} KG", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Region: ${ord.shippingAddress}", fontSize = 12.sp)
                        Text(text = "Payout: ₹${flatPayoutRule}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { 
                                viewModel.deliveryAcceptOrder(
                                    ord.id, 
                                    currentUser?.phoneNumber ?: "8888888888", 
                                    currentUser?.name ?: "Ramesh Kumar"
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isHi) "डिलीवरी स्वीकार करें 🚴‍♂️" else "Accept Delivery Order 🚴‍♂️")
                        }
                    }
                }
            }
        }
    }
}

// 10. ADMIN DASHBOARD & CRUD PANEL
@Composable
fun AdminDashboardScreen(navController: NavController, viewModel: FreshSabziViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isHi = currentLang == "hi"

    val vegetablesList by viewModel.vegetables.collectAsStateWithLifecycle()
    val ordersList by viewModel.allOrders.collectAsStateWithLifecycle()
    val usersList by viewModel.allUsers.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("STOCK") } // STOCK, ORDERS, USERS

    // Analytics: Gross earnings sums
    val totalOrdersCount = ordersList.size
    val totalSales = ordersList.sumOf { it.grandTotal }
    val totalWeightShipped = ordersList.sumOf { it.totalWeightKg }

    // Forms fields for Add/Edit Vegetables
    var currentEditingVegId by remember { mutableIntStateOf(0) } // 0 means new veggie
    var vegName by remember { mutableStateOf("") }
    var vegNameHindi by remember { mutableStateOf("") }
    var vegCategory by remember { mutableStateOf("Leafy Vegetables") }
    var vegPrice by remember { mutableStateOf("") }
    var vegStock by remember { mutableStateOf("") }
    var vegOrganic by remember { mutableStateOf(false) }
    var vegDesc by remember { mutableStateOf("") }
    var vegDescHindi by remember { mutableStateOf("") }
    var vegDiscount by remember { mutableStateOf("") }
    var vegImageType by remember { mutableStateOf("spinach") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_screen")
    ) {
        
        // Header sum analytics cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Text(
                text = if (isHi) "प्रबंधक पैनल (Manager Amit Dashboard)" else "Amit Admin Control Panel 🛠️",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnalyticsBadgeCard(label = if (isHi) "कुल बिक्री" else "Total Revenue", value = "₹${"%.0f".format(totalSales)}", modifier = Modifier.weight(1f))
                AnalyticsBadgeCard(label = if (isHi) "कुल ऑर्डर" else "Total Orders", value = "$totalOrdersCount", modifier = Modifier.weight(1f))
                AnalyticsBadgeCard(label = if (isHi) "वितरित वजन" else "Shipped", value = "${"%.1f".format(totalWeightShipped)} KG", modifier = Modifier.weight(1f))
            }
        }

        // Segment Tabs row selector
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))) {
            listOf("STOCK", "ORDERS", "USERS").forEach { tab ->
                val label = when (tab) {
                    "ORDERS" -> if (isHi) "ऑर्डर मॉनिटर" else "Order Monitor"
                    "USERS" -> if (isHi) "उपयोगकर्ता" else "Users List"
                    else -> if (isHi) "इन्वेंट्री CRUD" else "Inventory Stock"
                }
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (activeTab) {
                "STOCK" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section: Add or Edit Vegetable Forms
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (currentEditingVegId == 0) (if (isHi) "नई सब्जी जोड़ें +" else "Add New Vegetable +") else (if (isHi) "सब्जी संशोधित करें ✏️" else "Edit Existing Vegetable ✏️"),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(value = vegName, onValueChange = { vegName = it }, label = { Text("English Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(value = vegNameHindi, onValueChange = { vegNameHindi = it }, label = { Text("Hindi Name (हिंदी नाम)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    // Row inputs price & stock
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(value = vegPrice, onValueChange = { vegPrice = it }, label = { Text("Price/KG (₹)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                                        OutlinedTextField(value = vegStock, onValueChange = { vegStock = it }, label = { Text("Stock (KG)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(value = vegDiscount, onValueChange = { vegDiscount = it }, label = { Text("Offer Discount %") }, modifier = Modifier.weight(1.5f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                                        // Image vector enum selection
                                        OutlinedTextField(value = vegImageType, onValueChange = { vegImageType = it }, label = { Text("Vector Graphic Type") }, placeholder = { Text("spinach, onion, carrot, apple") }, modifier = Modifier.weight(2f), singleLine = true)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(value = vegDesc, onValueChange = { vegDesc = it }, label = { Text("Description (EN)") }, modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(value = vegDescHindi, onValueChange = { vegDescHindi = it }, label = { Text("विवरण (HI)") }, modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = vegOrganic, onCheckedChange = { vegOrganic = it })
                                        Text(if (isHi) "जैविक / ऑर्गेनिक सब्जियां" else "Certified Organic Vegetable")
                                    }

                                    // Category selector chips Row
                                    Text("Category:", fontSize = 11.sp, color = Color.Gray)
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(listOf("Leafy Vegetables", "Root Vegetables", "Fruits", "Herbs", "Organic Vegetables")) { c ->
                                            FilterChip(
                                                selected = vegCategory == c,
                                                onClick = { vegCategory = c },
                                                label = { Text(c, fontSize = 9.sp) }
                                            )
                                        }
                                    }

                                    // Submit Form actions button
                                    Button(
                                        onClick = {
                                            if (vegName.trim().isEmpty() || vegPrice.trim().isEmpty() || vegStock.trim().isEmpty()) {
                                                viewModel.showNotification("Specify Name, Price and Stock values.", "नाम, मूल्य और स्टॉक निर्दिष्ट करें।")
                                            } else {
                                                viewModel.adminAddOrUpdateVegetable(
                                                    id = currentEditingVegId,
                                                    name = vegName,
                                                    nameHindi = vegNameHindi,
                                                    category = vegCategory,
                                                    price = vegPrice.toDoubleOrNull() ?: 10.0,
                                                    stock = vegStock.toDoubleOrNull() ?: 50.0,
                                                    isOrganic = vegOrganic,
                                                    desc = vegDesc,
                                                    descHindi = vegDescHindi,
                                                    imageType = vegImageType,
                                                    discount = vegDiscount.toIntOrNull() ?: 0
                                                )
                                                // Clear items
                                                currentEditingVegId = 0
                                                vegName = ""
                                                vegNameHindi = ""
                                                vegPrice = ""
                                                vegStock = ""
                                                vegDiscount = ""
                                                vegOrganic = false
                                                vegDesc = ""
                                                vegDescHindi = ""
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (currentEditingVegId == 0) "Submit Item" else "Apply Changes")
                                    }
                                    if (currentEditingVegId != 0) {
                                        TextButton(
                                            onClick = { currentEditingVegId = 0; vegName = "" },
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                }
                            }
                        }

                        // Listing Item Inventory lists for quick edit / delete
                        item {
                            Text("Inventory Stock Monitor Checklist:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        items(vegetablesList) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    VegetableGraphic(type = item.imageType, modifier = Modifier.size(45.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "Price: ₹${item.pricePerKg} | Stock: ${item.stockKg} kg", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Row {
                                        IconButton(
                                            onClick = {
                                                currentEditingVegId = item.id
                                                vegName = item.name
                                                vegNameHindi = item.nameHindi
                                                vegCategory = item.category
                                                vegPrice = item.pricePerKg.toString()
                                                vegStock = item.stockKg.toString()
                                                vegOrganic = item.isOrganic
                                                vegDesc = item.description
                                                vegDescHindi = item.descriptionHindi
                                                vegDiscount = item.discountPercent.toString()
                                                vegImageType = item.imageType
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                        }
                                        IconButton(onClick = { viewModel.adminDeleteVegetable(item) }) {
                                            Icon(Icons.Default.Delete, "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "ORDERS" -> {
                    // Monitor all orders
                    if (ordersList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(if (isHi) "कोई ऑर्डर प्राप्त नहीं हुआ!" else "No customer orders matching yet!", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(ordersList) { ord ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Order ID: #${ord.id}", fontWeight = FontWeight.Bold)
                                            Text("Total: ₹${ord.grandTotal}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Text("User: ${ord.userName} (${ord.userPhone})", fontSize = 12.sp)
                                        Text("Status: ${ord.status}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                        Text("Address: ${ord.shippingAddress}", fontSize = 11.sp, color = Color.Gray)
                                        
                                        // Quick advance status dropdown/button
                                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            AssistChip(
                                                onClick = { viewModel.deliveryUpdateStatus(ord.id, "ACCEPTED") },
                                                label = { Text("Accept") }
                                            )
                                            AssistChip(
                                                onClick = { viewModel.deliveryUpdateStatus(ord.id, "ON_THE_WAY") },
                                                label = { Text("Ship") }
                                            )
                                            AssistChip(
                                                onClick = { viewModel.deliveryUpdateStatus(ord.id, "DELIVERED") },
                                                label = { Text("Deliver") }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "USERS" -> {
                    // Show registered user accounts list
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(usersList) { user ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(user.name, fontWeight = FontWeight.Bold)
                                        Text("Phone: ${user.phoneNumber}", fontSize = 12.sp, color = Color.Gray)
                                        Text("Address: ${user.address}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(user.role, fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsBadgeCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
