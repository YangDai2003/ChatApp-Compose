package com.yangdai.chatapp.core

import com.yangdai.chatapp.R

enum class SnackbarMsgEnum(private val stringResId: Int) {
    NONE(R.string.toast_none),
    ACCEPT(R.string.toast_accept),
    CANCEL(R.string.toast_cancel),
    SEND(R.string.toast_send),
    FRIEND(R.string.toast_friends),
    REQUESTED(R.string.toast_requested),
    UNBLOCK(R.string.toast_unblock),
    BLOCKED(R.string.toast_blocked),
    BLOCK(R.string.toast_block),
    LOGIN_SUCCESS(R.string.success),
    LOGIN_FAIL(R.string.fail),
    SIGNUP_SUCCESS(R.string.sign_up_success),
    SIGNUP_FAIL(R.string.sign_up_fail),
    UPDATE(R.string.profile_updated),
    SAVED(R.string.profile_saved),
    UPDATE_FAIL(R.string.update_failed),
    SIGNOUT(R.string.log_out);


    fun getStringResource(): Int {
        // 根据stringResId获取相应的String资源
        return stringResId
    }
}