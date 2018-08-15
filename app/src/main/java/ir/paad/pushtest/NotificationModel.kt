package ir.paad.pushtest

import android.graphics.Color


class NotificationModel {
    
    var title: String? = null

    var bigTitle: String? = null

    var message: String? = null

    var bigMessage: String? = null

    var summary: String? = null

    var iconUrl: String? = null

    var smallIconUrl:String?= null

    var imageUrl: String? = "test"

    var notificationId: String? = null

    var sendTime: String? = null

    var bgColor: Int? = Color.WHITE

    var buttonActionData: String? = null

    var sound: String? = "default..mp3"

    var vibrate: Boolean = false

    var showToUser: Boolean = true

    var ledColor: Int? = Color.GREEN

    var type: Type = Type.BIG_IMAGE

    enum class Type {
        BIG_TEXT, BIG_IMAGE
    }


}
