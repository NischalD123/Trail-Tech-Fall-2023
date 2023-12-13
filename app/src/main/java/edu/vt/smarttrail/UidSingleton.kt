package edu.vt.smarttrail

object UidSingleton {
    private var uid: String = ""

    fun setUid(uid: String) {
        this.uid = uid
    }

    fun getUid(): String {
        return uid
    }
}
