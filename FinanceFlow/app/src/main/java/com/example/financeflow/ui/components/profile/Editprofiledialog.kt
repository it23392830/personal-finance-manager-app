package com.example.financeflow.ui.components.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val CardWhite    = Color(0xFFFFFFFF)
private val GreenSave    = Color(0xFF16A34A)
private val PurpleCancel = Color(0xFF9C6CF7)
private val FieldBg      = Color(0xFFF5F3FF)
private val FieldBorder  = Color(0xFFD0C4E8)
private val DarkText     = Color(0xFF1A1A1A)
private val LabelGray    = Color(0xFF555555)

@Composable
fun EditProfileDialog(onDismiss: () -> Unit = {}) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("Kavindu Silva") }
    var email by remember { mutableStateOf("kavindusilva123@gmail.com") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .wrapContentHeight()
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(25.dp))
                    .background(CardWhite, RoundedCornerShape(25.dp))
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Text(
                        text = "Personal Information",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    EditProfileField(
                        label = "Full Name",
                        value = fullName,
                        onChange = { fullName = it }
                    )

                    EditProfileField(
                        label = "E mail",
                        value = email,
                        onChange = { email = it }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenSave,
                                contentColor = CardWhite
                            )
                        ) {
                            Text(
                                text = "Save Changes",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PurpleCancel,
                                contentColor = CardWhite
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditProfileField(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = LabelGray
        )
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg,
                focusedBorderColor = FieldBorder,
                unfocusedBorderColor = FieldBorder,
                cursorColor = DarkText,
                focusedTextColor = DarkText,
                unfocusedTextColor = DarkText
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "EditProfileDialog")
@Composable
private fun PreviewEditProfileDialog() {
    MaterialTheme {
        EditProfileDialog()
    }
}
