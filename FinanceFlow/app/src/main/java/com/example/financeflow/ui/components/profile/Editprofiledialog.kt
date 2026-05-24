package com.example.financeflow.ui.components.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private val EditSaveGreen = Color(0xFF16A34A)
private val EditCancelPurple = Color(0xFF9C6CF7)

private data class EditProfileDialogPalette(
    val card: Color,
    val field: Color,
    val fieldBorder: Color,
    val text: Color,
    val muted: Color
)

private fun editProfileDialogPalette(isDarkTheme: Boolean): EditProfileDialogPalette =
    if (isDarkTheme) {
        EditProfileDialogPalette(
            card = Color(0xFF241F30),
            field = Color(0xFF2F293A),
            fieldBorder = Color(0xFF5C4F72),
            text = Color(0xFFF4EEFF),
            muted = Color(0xFFB8AEC8)
        )
    } else {
        EditProfileDialogPalette(
            card = Color(0xFFFFFFFF),
            field = Color(0xFFF5F3FF),
            fieldBorder = Color(0xFFD0C4E8),
            text = Color(0xFF1A1A1A),
            muted = Color(0xFF555555)
        )
    }

@Composable
fun EditProfileDialog(
    isDarkTheme: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("Kavindu Silva") }
    var email by remember { mutableStateOf("kavindusilva123@gmail.com") }
    val palette = editProfileDialogPalette(isDarkTheme)

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
                    .background(palette.card, RoundedCornerShape(25.dp))
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Text(
                        text = "Personal Information",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.text
                    )

                    EditProfileField(
                        label = "Full Name",
                        value = fullName,
                        onChange = { fullName = it },
                        palette = palette
                    )

                    EditProfileField(
                        label = "E mail",
                        value = email,
                        onChange = { email = it },
                        palette = palette
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
                                containerColor = EditSaveGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Save Changes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EditCancelPurple,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
    onChange: (String) -> Unit,
    palette: EditProfileDialogPalette
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.muted
        )
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = palette.field,
                unfocusedContainerColor = palette.field,
                focusedBorderColor = palette.fieldBorder,
                unfocusedBorderColor = palette.fieldBorder,
                cursorColor = palette.text,
                focusedTextColor = palette.text,
                unfocusedTextColor = palette.text
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewEditProfileDialog() {
    MaterialTheme {
        EditProfileDialog()
    }
}
