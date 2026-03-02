package com.allterra.presentation.auth

import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.allterra_logo
import allterra.composeapp.generated.resources.allterra_named_logo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.core.ui.UiState
import com.allterra.domain.model.AppLanguage
import com.allterra.presentation.common.components.LanguageSwitchLabel
import com.allterra.presentation.common.components.resolveBackdrop
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.localization.languageDisplayName
import com.allterra.presentation.root.AuthScreen
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

    when (mode) {
        AuthScreen.LOGIN -> LoginScreen(
            state = state,
            backdropIndex = backdropIndex,
            selectedLanguage = selectedLanguage,
            onLanguageSwitchClick = onLanguageSwitchClick,
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
            selectedLanguage = selectedLanguage,
            onLanguageSwitchClick = onLanguageSwitchClick,
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

@Composable
private fun LoginScreen(
    state: AuthFormState,
    backdropIndex: Int,
    selectedLanguage: AppLanguage,
    onLanguageSwitchClick: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onOpenRegister: () -> Unit,
) {
    val strings = appStrings()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .windowInsetsPadding(WindowInsets.ime),
    ) {
        Image(
            painter = painterResource(resolveBackdrop(backdropIndex)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x1A000000), Color(0x600D2A3B))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.14f))
            Image(
                painter = painterResource(Res.drawable.allterra_named_logo),
                contentDescription = strings.appName,
                modifier = Modifier.size(250.dp),
            )

            Spacer(modifier = Modifier.weight(0.06f))

            AuthFields(
                state = state,
                onNameChanged = {},
                onEmailChanged = onEmailChanged,
                onPasswordChanged = onPasswordChanged,
                onSubmit = onSubmit,
                submitTitle = strings.loginAction,
            )

            Text(
                text = strings.registerLink,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable(onClick = onOpenRegister),
                color = Color(0xFF146B73),
            )

            Spacer(modifier = Modifier.weight(1f))

            FooterRow(
                footer = strings.appFooter,
                languageLabel = languageDisplayName(selectedLanguage, strings),
                onLanguageSwitchClick = onLanguageSwitchClick,
            )
        }
    }
}

@Composable
private fun RegisterScreen(
    state: AuthFormState,
    selectedLanguage: AppLanguage,
    onLanguageSwitchClick: () -> Unit,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onOpenLogin: () -> Unit,
) {
    val strings = appStrings()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAECEE))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .windowInsetsPadding(WindowInsets.ime),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.22f))

            Text(
                text = strings.registerTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF0E7E8A),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = strings.registerSubtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF549CA4),
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
            )

            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(Res.drawable.allterra_logo),
                    contentDescription = null,
                    modifier = Modifier.size(260.dp),
                    alpha = 0.35f,
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AuthFields(
                        state = state,
                        onNameChanged = onNameChanged,
                        onEmailChanged = onEmailChanged,
                        onPasswordChanged = onPasswordChanged,
                        onSubmit = onSubmit,
                        submitTitle = strings.registerAction,
                        showNameField = true,
                        compact = true,
                    )
                }
            }

            Text(
                text = strings.loginLink,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable(onClick = onOpenLogin),
                color = Color(0xFF146B73),
            )

            Spacer(modifier = Modifier.weight(1f))
            FooterRow(
                footer = strings.appFooter,
                languageLabel = languageDisplayName(selectedLanguage, strings),
                onLanguageSwitchClick = onLanguageSwitchClick,
            )
        }
    }
}

@Composable
private fun AuthFields(
    state: AuthFormState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    submitTitle: String,
    showNameField: Boolean = false,
    compact: Boolean = false,
) {
    val strings = appStrings()

    val fieldModifier = if (compact) {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
    } else {
        Modifier.fillMaxWidth()
    }

    if (showNameField) {
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChanged,
            modifier = fieldModifier,
            label = { Text(strings.nameLabel) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
        )
    }

    OutlinedTextField(
        value = state.email,
        onValueChange = onEmailChanged,
        modifier = if (showNameField) fieldModifier.padding(top = 8.dp) else fieldModifier,
        label = { Text(strings.emailLabel) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
    )

    OutlinedTextField(
        value = state.password,
        onValueChange = onPasswordChanged,
        modifier = fieldModifier.padding(top = 8.dp),
        label = { Text(strings.passwordLabel) },
        shape = RoundedCornerShape(12.dp),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
    )

    Button(
        onClick = onSubmit,
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(if (compact) 0.42f else 0.36f),
        enabled = state.uiState !is UiState.Loading,
    ) {
        Text(submitTitle)
    }

    when (val uiState = state.uiState) {
        UiState.Idle -> Unit
        UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
        is UiState.Error -> Text(
            text = uiState.message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 6.dp),
            textAlign = TextAlign.Center,
        )

        is UiState.Success -> Text(
            text = strings.authSuccess,
            color = Color(0xFF0E7E8A),
            modifier = Modifier.padding(top = 6.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FooterRow(
    footer: String,
    languageLabel: String,
    onLanguageSwitchClick: () -> Unit,
) {
    val footerText = "$footer ${currentSystemYear()}"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = footerText, color = Color(0xFF0E7E8A))
        LanguageSwitchLabel(
            title = languageLabel,
            onClick = onLanguageSwitchClick,
        )
    }
}

@OptIn(ExperimentalTime::class)
private fun currentSystemYear(): Int {
    return Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year
}
