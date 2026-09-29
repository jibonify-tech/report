package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    language: String,
    onLanguageToggle: () -> Unit,
    onLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onRegister: (String, String, String, String, (Boolean, String?) -> Unit) -> Unit,
    onQuickLogin: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("01711111111") }
    var loginPassword by remember { mutableStateOf("1234") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Register Form State
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Language Toggle in Top Corner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onLanguageToggle() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Language", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "bn") "English" else "বাংলা",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Logo & Title
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Jibonify Daily",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (language == "bn") "আপনার টাকা। আপনার দিন। আপনার সম্পূর্ণ হিসাব।"
                else "Your Money. Your Day. Your Complete Hisab.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Switcher (Login / Register)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (language == "bn") "লগ ইন (Log In)" else "Log In",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (language == "bn") "নতুন একাউন্ট (Sign Up)" else "Sign Up",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Forms Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedTab == 0) {
                        // === LOGIN FORM ===
                        Text(
                            text = if (language == "bn") "আপনার একাউন্টে প্রবেশ করুন" else "Welcome Back",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = {
                                loginIdentifier = it
                                loginError = null
                            },
                            label = { Text(if (language == "bn") "ফোন নম্বর বা ইমেইল" else "Phone or Email") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_input_phone"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = {
                                loginPassword = it
                                loginError = null
                            },
                            label = { Text(if (language == "bn") "পাসওয়ার্ড / পিন (PIN)" else "Password / PIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_input_password"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        AnimatedVisibility(visible = loginError != null) {
                            loginError?.let { err ->
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (loginIdentifier.isBlank() || loginPassword.isBlank()) {
                                    loginError = if (language == "bn") "ফোন ও পাসওয়ার্ড লিখুন" else "Please enter phone and password"
                                    return@Button
                                }
                                isSubmitting = true
                                onLogin(loginIdentifier, loginPassword) { success, msg ->
                                    isSubmitting = false
                                    if (!success) {
                                        loginError = msg ?: (if (language == "bn") "লগইন ব্যর্থ হয়েছে" else "Login failed")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isSubmitting
                        ) {
                            Text(
                                text = if (language == "bn") "লগ ইন করুন" else "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    } else {
                        // === REGISTER FORM ===
                        Text(
                            text = if (language == "bn") "নতুন প্রোফাইল তৈরি করুন" else "Create New Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text(if (language == "bn") "আপনার পূর্ণ নাম" else "Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text(if (language == "bn") "মোবাইল নম্বর (লগইন আইডি)" else "Phone Number (Login ID)") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text(if (language == "bn") "ইমেইল (ঐচ্ছিক)" else "Email (Optional)") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text(if (language == "bn") "পাসওয়ার্ড / পিন (PIN)" else "Password / PIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regConfirmPassword,
                            onValueChange = { regConfirmPassword = it },
                            label = { Text(if (language == "bn") "পাসওয়ার্ড নিশ্চিত করুন" else "Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        AnimatedVisibility(visible = regError != null) {
                            regError?.let { err ->
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (regName.isBlank() || regPhone.isBlank() || regPassword.isBlank()) {
                                    regError = if (language == "bn") "সকল প্রয়োজনীয় তথ্য পূরণ করুন" else "Please fill all required fields"
                                    return@Button
                                }
                                if (regPassword != regConfirmPassword) {
                                    regError = if (language == "bn") "উভয় পাসওয়ার্ড মেলেনি" else "Passwords do not match"
                                    return@Button
                                }
                                isSubmitting = true
                                onRegister(regName, regPhone, regEmail, regPassword) { success, msg ->
                                    isSubmitting = false
                                    if (!success) {
                                        regError = msg ?: (if (language == "bn") "রেজিস্ট্রেশন ব্যর্থ হয়েছে" else "Registration failed")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isSubmitting
                        ) {
                            Text(
                                text = if (language == "bn") "একাউন্ট তৈরি করুন" else "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo Switcher (Instant 1-tap testing for Admin, Rahim, Karim)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == "bn") "⚡ দ্রুত পরীক্ষামূলক লগইন (Demo Switcher):"
                        else "⚡ Quick Demo Access:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onQuickLogin("admin") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "bn") "এডমিন" else "Admin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { onQuickLogin("rahim") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Rahim", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "bn") "রহিম" else "Rahim",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { onQuickLogin("karim") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Karim", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "bn") "করিম" else "Karim",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy & Account Isolation Guarantee Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == "bn")
                        "কোউ কারো একাউন্ট দেখতে পাবে না এডমিন ছাড়া। আপনার ডাটা সম্পূর্ণ ব্যক্তিগত ও সুরক্ষিত।"
                    else
                        "Nobody can see anyone else's account except Admin. Your financial data is private and secure.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
