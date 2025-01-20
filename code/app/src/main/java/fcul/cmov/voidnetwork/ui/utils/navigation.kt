package fcul.cmov.voidnetwork.ui.utils

import androidx.navigation.NavBackStackEntry

fun NavBackStackEntry.getArgument(arg: String): String {
    val argument = arguments?.getString(arg)
    requireNotNull(argument) { "Argument $arg is missing" }
    return argument
}

fun String.args(vararg args: Pair<String, Any>): String {
    return args.fold(this) { acc, (key, value) ->
        acc.replace("{$key}", value.toString())
    }
}
