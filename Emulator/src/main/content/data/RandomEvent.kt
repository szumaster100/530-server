package content.data

object RandomEvent {
    @JvmStatic
    fun logout(): String = "/save:${javaClass.simpleName}-logout"
    @JvmStatic
    fun save(): String = "/save:original-loc"
    @JvmStatic
    fun score(): String = "/save:${javaClass.simpleName}:score"
}