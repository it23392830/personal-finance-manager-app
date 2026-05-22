package com.example.financeflow.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

 
// Color tokens  (app-wide palette)
 
private val BgPurple   = Color(0xFFEDE2FF)
private val CardWhite  = Color(0xFFFFFFFF)
private val PurpleBtn  = Color(0xFF9B72CF)
private val PurpleDark = Color(0xFF6A3FA0)
private val RedLogout  = Color(0xFFE53935)
private val FieldBorder = Color(0xFFD0C4E8)
private val LabelGray  = Color(0xFF888888)
private val DarkText   = Color(0xFF1A1A1A)

 
// Hardcoded dummy data
 
private const val USER_NAME  = "Kavindu Silva"
private const val USER_EMAIL = "kavindusilva123@gmail.com"

private val currencyOptions = listOf(
    "LKR (Sri Lankan Rupee)", "USD (US Dollar)", "EUR (Euro)", "GBP (British Pound)"
)
private val trackerOptions = listOf("Real-Time", "Daily Summary", "Weekly Summary")

 
// ProfileScreen
//
// Root composable for the Profile section of FinanceFlow.
// Uses LazyColumn for full vertical scrollability.
//
// Dialog / screen flow:
//   Edit Profile button    → showEditProfilePopup = true
//       → EditProfileDialog overlay (Personal Information form)
//
//   Change Password button → showChangePasswordDialog = true
//       → ChangePasswordDialog overlay
//
//   Log Out button         → onNavigateToLogout()
//       → navigates to LogoutScreen (handled by AppNavGraph)
//
// All state is local — no ViewModel, no repository, no backend.
 
@Composable
fun ProfileScreen(
    onNavigateBack:     () -> Unit = {},
    onNavigateToLogout: () -> Unit = {}     // wired to navController in AppNavGraph
) {
    // ── Settings state ─────────────────────────────────────────────────────
    var selectedCurrency  by remember { mutableStateOf(currencyOptions[0]) }
    var selectedTracker   by remember { mutableStateOf(trackerOptions[0]) }
    var currencyExpanded  by remember { mutableStateOf(false) }
    var trackerExpanded   by remember { mutableStateOf(false) }

    // ── Notification switch state ──────────────────────────────────────────
    var pushNotifications by remember { mutableStateOf(true) }
    var dailyReminder     by remember { mutableStateOf(true) }
    var weeklyReport      by remember { mutableStateOf(true) }

    // ── Dialog visibility state ────────────────────────────────────────────
    // true  → ChangePasswordDialog floats above the screen
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    // true  → EditProfileDialog floats above the screen
    var showEditProfilePopup by remember { mutableStateOf(false) }

    // ── Main scrollable layout ─────────────────────────────────────────────
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPurple),
        contentPadding = PaddingValues(
            start  = 16.dp, end    = 16.dp,
            top    = 20.dp, bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── 1. Header card ─────────────────────────────────────────────────
        item { ProfileHeaderCard(onDarkModeClick = { /* UI stub */ }) }

        // ── 2. Profile image + Edit Profile button ─────────────────────────
        // Clicking "Edit Profile" sets showEditProfilePopup = true,
        // which renders EditProfileDialog as a floating overlay below.
        item { ProfileImageCard(onEditProfile = { showEditProfilePopup = true }) }

        // ── 3. User information (read-only fields) ─────────────────────────
        item { UserInformationCard(fullName = USER_NAME, email = USER_EMAIL) }

        // ── 4. Settings ────────────────────────────────────────────────────
        item {
            SettingsCard(
                selectedCurrency  = selectedCurrency,
                currencyExpanded  = currencyExpanded,
                onCurrencyExpand  = { currencyExpanded  = true  },
                onCurrencyDismiss = { currencyExpanded  = false },
                onCurrencySelect  = { selectedCurrency  = it; currencyExpanded  = false },
                selectedTracker   = selectedTracker,
                trackerExpanded   = trackerExpanded,
                onTrackerExpand   = { trackerExpanded   = true  },
                onTrackerDismiss  = { trackerExpanded   = false },
                onTrackerSelect   = { selectedTracker   = it; trackerExpanded   = false }
            )
        }

        // ── 5. Notifications ───────────────────────────────────────────────
        item {
            NotificationsCard(
                pushEnabled    = pushNotifications, onPushToggle   = { pushNotifications = it },
                dailyEnabled   = dailyReminder,     onDailyToggle  = { dailyReminder     = it },
                weeklyEnabled  = weeklyReport,      onWeeklyToggle = { weeklyReport      = it }
            )
        }

        // ── 6. Account actions ─────────────────────────────────────────────
        //   Change Password → showChangePasswordDialog = true
        //   Log Out         → navigate to LogoutScreen
        item {
            AccountActionsCard(
                onChangePassword = { showChangePasswordDialog = true },
                onLogOut         = { onNavigateToLogout() }
            )
        }
    }

    // ── ChangePasswordDialog overlay ───────────────────────────────────────
    // Rendered outside LazyColumn — floats above all content.
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false }
        )
    }

    // ── EditProfileDialog overlay ──────────────────────────────────────────
    // Rendered outside LazyColumn — floats above all content.
    // Triggered by the "Edit Profile" button inside ProfileImageCard.
    if (showEditProfilePopup) {
        EditProfileDialog(
            onDismiss = { showEditProfilePopup = false }
        )
    }
}

 
// ProfileHeaderCard
 
@Composable
private fun ProfileHeaderCard(onDarkModeClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left — "Profile" in purple
            Text(
                text       = "Profile",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = PurpleDark,
                modifier   = Modifier.width(72.dp)
            )
            // Center — name + email
            Column(
                modifier            = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text       = USER_NAME,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = DarkText
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = USER_EMAIL, fontSize = 11.sp, color = LabelGray)
            }
            // Right — dark-mode icon
            Icon(
                imageVector        = Icons.Default.DarkMode,
                contentDescription = "Dark Mode",
                tint               = LabelGray,
                modifier           = Modifier.size(26.dp).clickable { onDarkModeClick() }
            )
        }
    }
}

 
// ProfileImageCard
 
@Composable
private fun ProfileImageCard(onEditProfile: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar + camera badge
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFFB39DDB), PurpleDark)))
                        .border(3.dp, CardWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint               = CardWhite,
                        modifier           = Modifier.size(60.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PurpleBtn)
                        .border(2.dp, CardWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.CameraAlt,
                        contentDescription = "Change photo",
                        tint               = CardWhite,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Edit Profile button
            Button(
                onClick  = onEditProfile,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(44.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleBtn,
                    contentColor   = CardWhite
                )
            ) {
                Icon(
                    imageVector        = Icons.Default.Edit,
                    contentDescription = null,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Edit Profile", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

 
// UserInformationCard
 
@Composable
private fun UserInformationCard(fullName: String, email: String) {
    ProfileSectionCard(title = "User Information") {
        ReadOnlyField(label = "Full Name", value = fullName, leadingIcon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(14.dp))
        ReadOnlyField(label = "Email", value = email, leadingIcon = Icons.Default.Email)
    }
}

 
// SettingsCard
 
@Composable
private fun SettingsCard(
    selectedCurrency:  String, currencyExpanded:  Boolean,
    onCurrencyExpand:  () -> Unit, onCurrencyDismiss: () -> Unit, onCurrencySelect:  (String) -> Unit,
    selectedTracker:   String, trackerExpanded:   Boolean,
    onTrackerExpand:   () -> Unit, onTrackerDismiss:  () -> Unit, onTrackerSelect:   (String) -> Unit
) {
    ProfileSectionCard(title = "Settings") {
        ProfileDropdownField(
            label = "Base Currency", value = selectedCurrency,
            expanded = currencyExpanded, options = currencyOptions,
            onExpand = onCurrencyExpand, onDismiss = onCurrencyDismiss, onSelect = onCurrencySelect
        )
        Spacer(modifier = Modifier.height(14.dp))
        ProfileDropdownField(
            label = "Expense Tracker", value = selectedTracker,
            expanded = trackerExpanded, options = trackerOptions,
            onExpand = onTrackerExpand, onDismiss = onTrackerDismiss, onSelect = onTrackerSelect
        )
    }
}

 
// NotificationsCard
 
@Composable
private fun NotificationsCard(
    pushEnabled:   Boolean, onPushToggle:   (Boolean) -> Unit,
    dailyEnabled:  Boolean, onDailyToggle:  (Boolean) -> Unit,
    weeklyEnabled: Boolean, onWeeklyToggle: (Boolean) -> Unit
) {
    ProfileSectionCard(title = "Notifications") {
        NotificationSwitchRow("Push Notifications", "Receive app notifications",  pushEnabled,  onPushToggle)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
        NotificationSwitchRow("Daily Reminder",     "Remind to log expenses",     dailyEnabled, onDailyToggle)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
        NotificationSwitchRow("Weekly Report",      "Get weekly summaries",       weeklyEnabled, onWeeklyToggle)
    }
}

 
// AccountActionsCard
//
// Change Password → opens ChangePasswordDialog (via callback)
// Log Out         → navigates to LogoutScreen   (via callback)
 
@Composable
private fun AccountActionsCard(
    onChangePassword: () -> Unit,
    onLogOut:         () -> Unit
) {
    ProfileSectionCard(title = "Account") {

        // Change Password — purple outlined button
        OutlinedButton(
            onClick  = onChangePassword,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape    = RoundedCornerShape(14.dp),
            border   = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = PurpleDark)
        ) {
            Icon(
                imageVector        = Icons.Default.Lock,
                contentDescription = null,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Change Password", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Log Out — red outlined button
        OutlinedButton(
            onClick  = onLogOut,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape    = RoundedCornerShape(14.dp),
            border   = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = RedLogout)
        ) {
            Icon(
                imageVector        = Icons.Default.Logout,
                contentDescription = null,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Log Out", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

 
// ─── Shared / reusable sub-components ────────────────────────────────────────
 

@Composable
private fun ProfileSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String, leadingIcon: ImageVector) {
    Column {
        Text(
            text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            color = DarkText, modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value, onValueChange = {}, enabled = false, singleLine = true,
            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = leadingIcon, contentDescription = null,
                    tint = PurpleBtn, modifier = Modifier.size(20.dp))
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor   = Color(0xFFF8F4FF),
                disabledBorderColor      = FieldBorder,
                disabledTextColor        = DarkText,
                disabledLeadingIconColor = PurpleBtn
            )
        )
    }
}

@Composable
private fun ProfileDropdownField(
    label: String, value: String, expanded: Boolean,
    options: List<String>, onExpand: () -> Unit, onDismiss: () -> Unit, onSelect: (String) -> Unit
) {
    Column {
        Text(
            text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            color = DarkText, modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, FieldBorder, RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F4FF))
                    .clickable { onExpand() }
                    .padding(horizontal = 16.dp, vertical = 15.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = value, fontSize = 14.sp, color = DarkText)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand", tint = LabelGray
                    )
                }
            }
            DropdownMenu(
                expanded = expanded, onDismissRequest = onDismiss,
                modifier = Modifier.fillMaxWidth(0.88f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option, fontSize = 14.sp) },
                        onClick = { onSelect(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSwitchRow(
    title: String, subtitle: String, checked: Boolean, onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = LabelGray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = CardWhite, checkedTrackColor = PurpleBtn,
                uncheckedThumbColor = LabelGray, uncheckedTrackColor = Color(0xFFDDD0F0)
            )
        )
    }
}

 
// Previews
 
@Preview(showBackground = true, showSystemUi = true, name = "ProfileScreen – Full")
@Composable
fun PreviewProfileScreen() {
    MaterialTheme { ProfileScreen() }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF, name = "Profile Header")
@Composable
fun PreviewProfileHeaderCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) { ProfileHeaderCard {} }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF, name = "Account Actions Card")
@Composable
fun PreviewAccountActionsCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AccountActionsCard(onChangePassword = {}, onLogOut = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDE2FF, name = "Notifications Card")
@Composable
fun PreviewNotificationsCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NotificationsCard(
                pushEnabled = true,  onPushToggle  = {},
                dailyEnabled = true, onDailyToggle = {},
                weeklyEnabled = false, onWeeklyToggle = {}
            )
        }
    }
}