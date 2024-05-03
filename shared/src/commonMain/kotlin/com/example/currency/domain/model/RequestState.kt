package com.example.currency.domain.model

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    fun isLoading(): Boolean = this is Loading
    fun isError(): Boolean = this is Error
    fun isSuccess(): Boolean = this is Success

    fun getSuccessData() = (this as Success).data
    fun getErrorMessage(): String = (this as Error).message
}

//@Composable
//fun <T> RequestState<T>.DisplayResult(
//    onIdle: (@Composable () -> Unit)? = null,
//    onLoading: (@Composable () -> Unit)? = null,
//    onError: (@Composable (String) -> Unit)? = null,
//    onSuccess: @Composable (T) -> Unit,
//    transitionSpec: ContentTransform = scaleIn(tween(durationMillis = 400))
//            + fadeIn(tween(durationMillis = 800))
//            togetherWith scaleOut(tween(durationMillis = 400))
//            + fadeOut(
//        tween(durationMillis = 800)
//    )
//) {
//    AnimatedContent(
//        targetState = this,
//        transitionSpec = { transitionSpec },
//        label = "Content Animation"
//    ) { state ->
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            when(state) {
//                is RequestState.Idle -> {
//                    onIdle?.invoke()
//                }
//                is RequestState.Loading -> {
//                    onLoading?.invoke()
//                }
//                is RequestState.Error -> {
//                    onError?.invoke(state.getErrorMessage())
//                }
//                is RequestState.Success -> {
//                    onSuccess(state.getSuccessData())
//                }
//            }
//        }
//    }
//}