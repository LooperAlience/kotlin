package chela.kotlin.core

object ChSchema {
    object Setting{
        @JvmStatic val ID = "id"
        @JvmStatic val STYLE = "style"
        @JvmStatic val API = "api"
        @JvmStatic val DB = "db"
        @JvmStatic val I18N = "i18n"
        object db{
            @JvmStatic val IS_DEFAULT = "isDefault"
            @JvmStatic val SQL = "base"
            @JvmStatic val VER = "ver"
            @JvmStatic val CREATE = "create"
            @JvmStatic val UPDATE = "update"
        }
        object i18n{
            @JvmStatic val LANG = "lang"
            @JvmStatic val VER = "ver"
            @JvmStatic val DATA = "data"
        }
        object i18nData{
            @JvmStatic val IS_ONE = "isOne"
            @JvmStatic val VER = "ver"
            @JvmStatic val DATA = "data"
        }
    }
}
