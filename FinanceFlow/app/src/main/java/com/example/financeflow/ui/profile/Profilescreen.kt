package com.example.financeflow.ui.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.financeflow.ui.components.profile.ChangePasswordDialog
import com.example.financeflow.ui.components.profile.EditProfileDialog
import com.example.financeflow.viewmodel.ProfileViewModel
import com.example.financeflow.viewmodel.auth.AuthViewModel

private val BgPurple = Color(0xFFEDE2FF)
private val CardWhite = Color(0xFFFFFFFF)
private val PurpleBtn = Color(0xFF9B72CF)
private val PurpleDark = Color(0xFF6A3FA0)
private val RedLogout = Color(0xFFE53935)
private val FieldBorder = Color(0xFFD0C4E8)
private val LabelGray = Color(0xFF888888)
private val DarkText = Color(0xFF1A1A1A)

private val currencyOptions = listOf(
    "LKR", "USD", "EUR", "GBP"
)
private val trackerOptions = listOf("Real-Time", "Daily Summary", "Weekly Summary")

@Composable
fun ProfileScreen(
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToLogout: () -> Unit = {},
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val profile by profileViewModel.profile.collectAsState()
    val error by profileViewModel.error.collectAsState()
    val toastMessage by profileViewModel.toastMessage.collectAsState()
    var currencyExpanded by remember { mutableStateOf(false) }
    var trackerExpanded by remember { mutableStateOf(false) }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfilePopup by remember { mutableStateOf(false) }

    val palette = profilePalette(isDarkTheme)
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { profileViewModel.uploadProfileImage(it) }
    }

    LaunchedEffect(toastMessage, error) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            profileViewModel.clearMessages()
        }
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            profileViewModel.clearMessages()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.screenBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileHeaderCard(
                isDarkTheme = isDarkTheme,
                fullName = profile.fullName,
                email = profile.email,
                palette = palette
            )
        }
        item {
            ProfileImageCard(
                profileImage = profile.profileImage,
                onChangePhoto = { imageLauncher.launch("image/*") },
                onEditProfile = { showEditProfilePopup = true },
                palette = palette
            )
        }
        item {
            UserInformationCard(
                fullName = profile.fullName,
                email = profile.email,
                palette = palette
            )
        }
        item {
            SettingsCard(
                selectedCurrency = profile.baseCurrency,
                currencyExpanded = currencyExpanded,
                onCurrencyExpand = { currencyExpanded = true },
                onCurrencyDismiss = { currencyExpanded = false },
                onCurrencySelect = {
                    currencyExpanded = false
                    profileViewModel.updateProfile(profile.copy(baseCurrency = it))
                },
                selectedTracker = profile.expenseTracker,
                trackerExpanded = trackerExpanded,
                onTrackerExpand = { trackerExpanded = true },
                onTrackerDismiss = { trackerExpanded = false },
                onTrackerSelect = {
                    trackerExpanded = false
                    profileViewModel.updateProfile(profile.copy(expenseTracker = it))
                },
                palette = palette
            )
        }
        item {
            NotificationsCard(
                pushEnabled = profile.pushNotifications,
                onPushToggle = {
                    profileViewModel.updateNotificationSettings(it, profile.dailyReminder, profile.weeklyReport)
                },
                dailyEnabled = profile.dailyReminder,
                onDailyToggle = {
                    profileViewModel.updateNotificationSettings(profile.pushNotifications, it, profile.weeklyReport)
                },
                weeklyEnabled = profile.weeklyReport,
                onWeeklyToggle = {
                    profileViewModel.updateNotificationSettings(profile.pushNotifications, profile.dailyReminder, it)
                },
                palette = palette
            )
        }
        item {
            AccountActionsCard(
                onChangePassword = { showChangePasswordDialog = true },
                onLogOut = {
                    authViewModel.logout()
                    onNavigateToLogout()
                },
                palette = palette
            )
        }
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            isDarkTheme = isDarkTheme,
            onChangePassword = profileViewModel::changePassword,
            onDismiss = { showChangePasswordDialog = false }
        )
    }

    if (showEditProfilePopup) {
        EditProfileDialog(
            isDarkTheme = isDarkTheme,
            initialFullName = profile.fullName,
            initialEmail = profile.email,
            onSave = { fullName, email ->
                profileViewModel.updateProfile(profile.copy(fullName = fullName, email = email))
            },
            onDismiss = { showEditProfilePopup = false }
        )
    }
}

@Composable
private fun ProfileHeaderCard(
    isDarkTheme: Boolean,
    fullName: String,
    email: String,
    palette: ProfilePalette
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = palette.titleColor,
                modifier = Modifier.width(72.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = fullName.ifBlank { "FinanceFlow User" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.primaryTextColor
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = email.ifBlank { "No email found" },
                    fontSize = 11.sp,
                    color = palette.secondaryTextColor
                )
            }
        }
    }
}

@Composable
private fun ProfileImageCard(
    profileImage: String,
    onChangePhoto: () -> Unit,
    onEditProfile: () -> Unit,
    palette: ProfilePalette
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFFB39DDB), PurpleDark)))
                        .border(3.dp, palette.cardColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImage.isNotBlank()) {
                        AsyncImage(
                            model = profileImage,
                            contentDescription = "Profile image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = CardWhite,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PurpleBtn)
                        .border(2.dp, palette.cardColor, CircleShape)
                        .clickable { onChangePhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change photo",
                        tint = CardWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onEditProfile,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(44.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleBtn,
                    contentColor = CardWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Edit Profile", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun UserInformationCard(
    fullName: String,
    email: String,
    palette: ProfilePalette
) {
    ProfileSectionCard(title = "User Information", palette = palette) {
        ReadOnlyField(
            label = "Full Name",
            value = fullName,
            leadingIcon = Icons.Default.Person,
            palette = palette
        )
        Spacer(modifier = Modifier.height(14.dp))
        ReadOnlyField(
            label = "Email",
            value = email,
            leadingIcon = Icons.Default.Email,
            palette = palette
        )
    }
}

@Composable
private fun SettingsCard(
    selectedCurrency: String,
    currencyExpanded: Boolean,
    onCurrencyExpand: () -> Unit,
    onCurrencyDismiss: () -> Unit,
    onCurrencySelect: (String) -> Unit,
    selectedTracker: String,
    trackerExpanded: Boolean,
    onTrackerExpand: () -> Unit,
    onTrackerDismiss: () -> Unit,
    onTrackerSelect: (String) -> Unit,
    palette: ProfilePalette
) {
    ProfileSectionCard(title = "Settings", palette = palette) {
        ProfileDropdownField(
            label = "Base Currency",
            value = selectedCurrency,
            expanded = currencyExpanded,
            options = currencyOptions,
            onExpand = onCurrencyExpand,
            onDismiss = onCurrencyDismiss,
            onSelect = onCurrencySelect,
            palette = palette
        )
        Spacer(modifier = Modifier.height(14.dp))
        ProfileDropdownField(
            label = "Expense Tracker",
            value = selectedTracker,
            expanded = trackerExpanded,
            options = trackerOptions,
            onExpand = onTrackerExpand,
            onDismiss = onTrackerDismiss,
            onSelect = onTrackerSelect,
            palette = palette
        )
    }
}

@Composable
private fun NotificationsCard(
    pushEnabled: Boolean,
    onPushToggle: (Boolean) -> Unit,
    dailyEnabled: Boolean,
    onDailyToggle: (Boolean) -> Unit,
    weeklyEnabled: Boolean,
    onWeeklyToggle: (Boolean) -> Unit,
    palette: ProfilePalette
) {
    ProfileSectionCard(title = "Notifications", palette = palette) {
        NotificationSwitchRow(
            title = "Push Notifications",
            subtitle = "Receive app notifications",
            checked = pushEnabled,
            onToggle = onPushToggle,
            palette = palette
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = palette.dividerColor)
        NotificationSwitchRow(
            title = "Daily Reminder",
            subtitle = "Remind to log expenses",
            checked = dailyEnabled,
            onToggle = onDailyToggle,
            palette = palette
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = palette.dividerColor)
        NotificationSwitchRow(
            title = "Weekly Report",
            subtitle = "Get weekly summaries",
            checked = weeklyEnabled,
            onToggle = onWeeklyToggle,
            palette = palette
        )
    }
}

@Composable
private fun AccountActionsCard(
    onChangePassword: () -> Unit,
    onLogOut: () -> Unit,
    palette: ProfilePalette
) {
    ProfileSectionCard(title = "Account", palette = palette) {
        OutlinedButton(
            onClick = onChangePassword,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PurpleDark)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Change Password", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedLogout)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Log Out", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    palette: ProfilePalette,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.cardColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primaryTextColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun ReadOnlyField(
    label: String,
    value: String,
    leadingIcon: ImageVector,
    palette: ProfilePalette
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.primaryTextColor,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = false,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = PurpleBtn,
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = palette.fieldBackground,
                disabledBorderColor = palette.fieldBorderColor,
                disabledTextColor = palette.primaryTextColor,
                disabledLeadingIconColor = PurpleBtn
            )
        )
    }
}

@Composable
private fun ProfileDropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    options: List<String>,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    palette: ProfilePalette
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.primaryTextColor,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, palette.fieldBorderColor, RoundedCornerShape(12.dp))
                    .background(palette.fieldBackground)
                    .clickable { onExpand() }
                    .padding(horizontal = 16.dp, vertical = 15.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = value, fontSize = 14.sp, color = palette.primaryTextColor)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = palette.secondaryTextColor
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss,
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
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    palette: ProfilePalette
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primaryTextColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = palette.secondaryTextColor)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CardWhite,
                checkedTrackColor = PurpleBtn,
                uncheckedThumbColor = LabelGray,
                uncheckedTrackColor = Color(0xFFDDD0F0)
            )
        )
    }
}

private data class ProfilePalette(
    val screenBackground: Color,
    val cardColor: Color,
    val titleColor: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val fieldBackground: Color,
    val fieldBorderColor: Color,
    val dividerColor: Color
)

private fun profilePalette(isDarkTheme: Boolean): ProfilePalette {
    return if (isDarkTheme) {
        ProfilePalette(
            screenBackground = Color(0xFF16131D),
            cardColor = Color(0xFF241F30),
            titleColor = Color(0xFFE2D5FF),
            primaryTextColor = Color(0xFFF4EEFF),
            secondaryTextColor = Color(0xFFB8AEC8),
            fieldBackground = Color(0xFF2F293A),
            fieldBorderColor = Color(0xFF5C4F72),
            dividerColor = Color(0xFF3A3348)
        )
    } else {
        ProfilePalette(
            screenBackground = BgPurple,
            cardColor = CardWhite,
            titleColor = PurpleDark,
            primaryTextColor = DarkText,
            secondaryTextColor = LabelGray,
            fieldBackground = Color(0xFFF8F4FF),
            fieldBorderColor = FieldBorder,
            dividerColor = Color(0xFFF0F0F0)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "ProfileScreen - Light")
@Composable
fun PreviewProfileScreen() {
    MaterialTheme { ProfileScreen() }
}

@Preview(showBackground = true, showSystemUi = true, name = "ProfileScreen - Dark")
@Composable
fun PreviewProfileScreenDark() {
    MaterialTheme { ProfileScreen(isDarkTheme = true) }
}
