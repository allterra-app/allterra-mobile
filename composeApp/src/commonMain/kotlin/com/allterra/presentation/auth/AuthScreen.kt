package com.allterra.presentation.auth

import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.mountain_color_no_bg
import allterra.composeapp.generated.resources.allterra_name_no_bg
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.core.ui.UiState
import com.allterra.domain.model.AppLanguage
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.root.AuthScreen
import com.allterra.presentation.theme.AllterraCategory
import com.allterra.presentation.theme.AllterraTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.jetbrains.compose.resources.painterResource

@Composable
fun AuthScreen(
    mode: AuthScreen,
    viewModel: AuthViewModel,
    backdropIndex: Int,
    selectedLanguage: AppLanguage,
    onLanguageSwitchClick: () -> Unit,
    onOpenLogin: () -> Unit,
    onOpenRegister: () -> Unit,
    onAuthorized: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = appStrings()

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            if (event is AuthEvent.Authorized) onAuthorized()
        }
    }

    AllterraTheme(category = AllterraCategory.Social) {
        when (mode) {
            AuthScreen.LOGIN -> LoginScreen(
                state = state,
                onEmailChanged = {
                    viewModel.clearError()
                    viewModel.onEmailChanged(it)
                },
                onPasswordChanged = {
                    viewModel.clearError()
                    viewModel.onPasswordChanged(it)
                },
                onSubmit = { viewModel.submit(AuthMode.Login, strings.authFieldValidation) },
                onOpenRegister = onOpenRegister,
            )

            AuthScreen.REGISTER -> RegisterScreen(
                state = state,
                onNameChanged = {
                    viewModel.clearError()
                    viewModel.onNameChanged(it)
                },
                onEmailChanged = {
                    viewModel.clearError()
                    viewModel.onEmailChanged(it)
                },
                onPasswordChanged = {
                    viewModel.clearError()
                    viewModel.onPasswordChanged(it)
                },
                onSubmit = { viewModel.submit(AuthMode.Register, strings.authFieldValidation) },
                onOpenLogin = onOpenLogin,
            )
        }
    }
}

@Composable
private fun LoginScreen(
    state: AuthFormState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onOpenRegister: () -> Unit,
) {
    val strings = appStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s10))
        
        Image(
            painter = painterResource(Res.drawable.mountain_color_no_bg),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s4))

        Text(
            text = strings.loginTitle,
            style = AllterraTheme.typography.displayL,
            color = AllterraTheme.colors.ink
        )

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s8))

        Column(verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s4)) {
            Text(strings.emailLabel, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.muted)
            AllterraInput(
                value = state.email,
                onValueChange = onEmailChanged,
                placeholder = "email@example.com"
            )

            Text(strings.passwordLabel, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.muted)
            var passwordVisible by remember { mutableStateOf(false) }
            AllterraInput(
                value = state.password,
                onValueChange = onPasswordChanged,
                placeholder = "••••••••",
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    AllterraIconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = AllterraTheme.colors.muted
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        AllterraButton(
            text = strings.loginAction,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.uiState !is UiState.Loading,
            onClick = onSubmit
        )

        if (state.uiState is UiState.Error) {
            Text(
                text = (state.uiState as UiState.Error).message,
                color = AllterraTheme.colors.crimson,
                style = AllterraTheme.typography.small,
                modifier = Modifier.padding(top = AllterraTheme.spacing.s2),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s8))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?", // TODO: Localize correctly
                style = AllterraTheme.typography.body,
                color = AllterraTheme.colors.muted
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = strings.registerLink,
                style = AllterraTheme.typography.bodyStrong,
                color = AllterraTheme.currentCategoryColors.color,
                modifier = Modifier.allterraClickable { onOpenRegister() }
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s10))
    }
}

@Composable
private fun RegisterScreen(
    state: AuthFormState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onOpenLogin: () -> Unit,
) {
    val strings = appStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s8))
        
        Image(
            painter = painterResource(Res.drawable.mountain_color_no_bg),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s4))

        Text(
            text = strings.registerTitle,
            style = AllterraTheme.typography.displayM,
            color = AllterraTheme.colors.ink,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        Column(verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
            LabelAndInput(strings.nameLabel, state.name, onNameChanged, "Your name")
            LabelAndInput(strings.emailLabel, state.email, onEmailChanged, "email@example.com")
            
            var passwordVisible by remember { mutableStateOf(false) }
            LabelAndInput(
                strings.passwordLabel, 
                state.password, 
                onPasswordChanged, 
                "••••••••",
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    AllterraIconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = AllterraTheme.colors.muted
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        // Terms Checkbox
        var termsAccepted by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AllterraCheckbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
            Spacer(modifier = Modifier.width(AllterraTheme.spacing.s3))
            Text(
                text = "I agree to Terms & Privacy", // TODO: Localize
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        AllterraButton(
            text = strings.registerAction,
            modifier = Modifier.fillMaxWidth(),
            enabled = termsAccepted && state.uiState !is UiState.Loading,
            onClick = onSubmit
        )

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        Text(
            text = strings.loginLink,
            style = AllterraTheme.typography.bodyStrong,
            color = AllterraTheme.colors.muted,
            modifier = Modifier.allterraClickable { onOpenLogin() }
        )

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s8))
    }
}

@Composable
private fun LabelAndInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s1)) {
        Text(label, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.muted)
        AllterraInput(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon
        )
    }
}

@OptIn(ExperimentalTime::class)
private fun currentSystemYear(): Int {
    return Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year
}
