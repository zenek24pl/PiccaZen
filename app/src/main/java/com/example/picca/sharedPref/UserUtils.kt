package com.example.picca.sharedPref

import android.content.Context
import android.content.SharedPreferences
import com.example.picca.model.User
import com.example.picca.model.UserType

class UserUtils(val context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("userLoginSession", 0)

    private val preferencesEditor = preferences.edit()

    private var isLogged = false
    private var uid: String? = null
    private var userType:UserType?=null
    fun saveSession(uid: String?, isLogged: Boolean,userType: UserType) {
        this.isLogged = isLogged
        this.uid = uid
        this.userType=userType
        preferencesEditor.putString(USER_UID, uid)
        preferencesEditor.putString(USER_TYPE,userType.name)
        preferencesEditor.putBoolean(LOGIN_SESSION, isLogged)
        preferencesEditor.commit()
    }

    fun getUserID(): String? {
        getPreferences()
        return preferences.getString(USER_UID, uid)
    }
    fun isLogged(): Boolean {
        getPreferences()
        return preferences.getBoolean(LOGIN_SESSION, isLogged)
    }
    fun getLogedUserType(): UserType? {
        getPreferences()
        if( preferences.getString(USER_TYPE,"")== UserType.GOOGLE.name) {
            return UserType.GOOGLE

        }else if(preferences.getString(USER_TYPE,"")== UserType.FIREBASE.name) {
            return UserType.FIREBASE
        }else if(preferences.getString(USER_TYPE,"")== UserType.FACEBOOK.name) {
            return UserType.FACEBOOK
        }else{
            return null
        }

    }
    fun logOut() {
        isLogged = false
        preferencesEditor.putString(USER_UID, uid)
        preferencesEditor.putBoolean(LOGIN_SESSION, isLogged)
        preferencesEditor.commit()
    }

    fun getPreferences(): SharedPreferences {
        return context.getSharedPreferences("userLoginSession", 0)
    }

    companion object {
        private const val LOGIN_SESSION = "userLoginSession"
        private const val USER_UID = "userUid"
        private const val USER_TYPE = "userType"

    }
}