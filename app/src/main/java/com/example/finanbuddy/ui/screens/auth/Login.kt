package com.example.finanbuddy.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.R
import com.example.finanbuddy.ui.navigation.AppScaffold
import com.example.finanbuddy.ui.navigation.NavigationAction
import com.example.finanbuddy.ui.navigation.Route
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoot(
    onNavAction: (NavigationAction) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    AuthRoot(
        onNavAction = onNavAction,
        successPopRoute = Route.Login,
        viewModel = viewModel
    ) { state, onAction, onGoogleAuthClick ->
        LoginScreen(
            state = state,
            onAction = onAction,
            onGoogleAuthClick = onGoogleAuthClick,
            onNavigateRegister = { onNavAction(NavigationAction.Navigate(Route.Register)) }
        )
    }
}

@Composable
fun RegisterRoot(
    onNavAction: (NavigationAction) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    AuthRoot(
        onNavAction = onNavAction,
        successPopRoute = Route.Register,
        viewModel = viewModel
    ) { state, onAction, onGoogleAuthClick ->
        RegisterScreen(
            state = state,
            onAction = onAction,
            onGoogleAuthClick = onGoogleAuthClick,
            onBack = { onNavAction(NavigationAction.Pop) },
            onNavigateLogin = {
                onNavAction(NavigationAction.NavigateAndPopTo(Route.Login, Route.Register))
            }
        )
    }
}

@Composable
private fun AuthRoot(
    onNavAction: (NavigationAction) -> Unit,
    successPopRoute: Route,
    viewModel: LoginViewModel,
    content: @Composable (LoginState, (LoginAction) -> Unit, () -> Unit) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onAction(LoginAction.HandleGoogleSignInResult(result.data))
    }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onNavAction(NavigationAction.NavigateAndPopTo(Route.Home, successPopRoute))
            viewModel.onAction(LoginAction.AuthNavigationConsumed)
        }
    }

    content(
        state,
        viewModel::onAction,
        {
            viewModel.getGoogleSignInIntent()?.let { intent -> launcher.launch(intent) }
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onGoogleAuthClick: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    var passwordVisible by retain { mutableStateOf(false) }

    AppScaffold(showBottomBar = false, showHeader = false, showActionButton = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.cd_back))
            }

            Spacer(modifier = Modifier.height(16.dp))

            AuthBrandHeader(
                title = stringResource(R.string.login_title),
                subtitle = stringResource(R.string.login_subtitle)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.label_email_address), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChange = { onAction(LoginAction.SetEmail(it)) },
                placeholder = { Text(stringResource(R.string.placeholder_email)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Email, contentDescription = stringResource(R.string.cd_email))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !state.isLoading,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(stringResource(R.string.label_password), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = { onAction(LoginAction.SetPassword(it)) },
                placeholder = { Text(stringResource(R.string.placeholder_password)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = stringResource(R.string.cd_password))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = stringResource(R.string.cd_toggle_password)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                enabled = !state.isLoading,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.forgot_password),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )

            state.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { onAction(LoginAction.SignInWithEmail) },
                enabled = !state.isLoading
            ) {
                Text(stringResource(R.string.btn_login), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            OrDivider()

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = onGoogleAuthClick,
                enabled = !state.isLoading
            ) {
                Text(stringResource(R.string.btn_continue_google), fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.no_account_prompt))
                Text(
                    text = stringResource(R.string.sign_up),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onNavigateRegister)
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onGoogleAuthClick: () -> Unit,
    onBack: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    var fullName by retain { mutableStateOf("") }
    var passwordVisible by retain { mutableStateOf(false) }

    AppScaffold(showBottomBar = false, showHeader = false, showActionButton = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                }
            }

            AuthBrandHeader(
                title = stringResource(R.string.register_title),
                subtitle = stringResource(R.string.register_subtitle)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.label_full_name), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text(stringResource(R.string.placeholder_full_name)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Person, contentDescription = stringResource(R.string.label_full_name))
                        },
                        singleLine = true,
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(stringResource(R.string.label_email_address), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.email,
                        onValueChange = { onAction(LoginAction.SetEmail(it)) },
                        placeholder = { Text(stringResource(R.string.placeholder_email)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Email, contentDescription = stringResource(R.string.cd_email))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(stringResource(R.string.label_password), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.password,
                        onValueChange = { onAction(LoginAction.SetPassword(it)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Lock, contentDescription = stringResource(R.string.cd_password))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = stringResource(R.string.cd_toggle_password)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(16.dp)
                    )

                    state.errorMessage?.let { message ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { onAction(LoginAction.RegisterWithEmail) },
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(R.string.btn_register), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    OrDivider(label = stringResource(R.string.or_label))

                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = onGoogleAuthClick,
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(R.string.btn_continue_google), fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.has_account_prompt))
                Text(
                    text = stringResource(R.string.log_in),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onNavigateLogin)
                )
            }
        }
    }
}

@Composable
private fun AuthBrandHeader(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = "Brand",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OrDivider(label: String = stringResource(R.string.or_divider)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Divider(modifier = Modifier.weight(1f))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )
        Divider(modifier = Modifier.weight(1f))
    }
}
