package github.ryuunoakaihitomi.powerpanel.desc

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

data class PowerInfo(
    var hasForceMode: Boolean = false,
    // 同时还需要转递富文本格式
    var label: CharSequence = "",
    // 由于每个的标签都不一样，可以用作标识符
    @StringRes var labelResId: Int = ResourcesCompat.ID_NULL,
    @DrawableRes var iconResId: Int = ResourcesCompat.ID_NULL
) {

    companion object {
        fun getLabelArray(array: Array<PowerInfo>) = getAttr(array) { label }
        fun getIconResIdArray(array: Array<PowerInfo>) = getAttr(array) { iconResId }

        private inline fun <reified T> getAttr(
            array: Array<PowerInfo>,
            member: PowerInfo.() -> T
        ): Array<T> {
            val list = mutableListOf<T>()
            array.forEach { list.add(it.member()) }
            return list.toTypedArray()
        }
    }
}