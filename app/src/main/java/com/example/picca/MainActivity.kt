package com.example.picca

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.picca.fragments.*
import com.example.picca.map.MapFragment
import com.example.picca.model.User
import com.example.picca.model.UserType
import com.example.picca.sharedPref.UserUtils
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), View.OnClickListener, ActivityInteractions,
    FragmentManager.OnBackStackChangedListener, DrawerLayout.DrawerListener {
    private var isDrawerMenuOpen: Boolean = false
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    var topBar: TopBarInteractions? = null


    var db = FirebaseFirestore.getInstance()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Buttons
        emailSignInButton.setOnClickListener(this)
        emailCreateAccountButton.setOnClickListener(this)
        signOutButton.setOnClickListener(this)
        sing_google.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        buttonFacebookSignout.setOnClickListener(this)

        callbackManager = CallbackManager.Factory.create()

        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // [START_EXCLUDE]
                updateUI(null,null)
                // [END_EXCLUDE]
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // [START_EXCLUDE]
                updateUI(null,null)
                // [END_EXCLUDE]
            }
        })
        activity_main_drawer_layout.addDrawerListener(this)


        val fragmentManager: FragmentManager = supportFragmentManager
        topBar = fragmentManager.findFragmentById(R.id.top_fragment) as TopBarInteractions
        fragmentManager.addOnBackStackChangedListener(this)
        topBar()?.showTopBar(false)
        setUpDrawerContent()
        isDrawerMenuOpen = false
    }

    private fun setUpDrawerContent() {
        activiy_navigation_menu_view.setNavigationItemSelectedListener(object:NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(p0: MenuItem): Boolean {
                selectDrawerItem(p0)
                return true
            }


        })
    }

    override fun onBackPressed() {
        if (isDrawerMenuOpen) {
            changeDrawerMenuState()
        } else {
            super.onBackPressed()
            activity_main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
        }
    }
    override fun changeDrawerMenuState() {
        if (isDrawerMenuOpen) {
            activity_main_drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            activity_main_drawer_layout.openDrawer(GravityCompat.START)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
             android.R.id.home-> {
                 activity_main_drawer_layout.openDrawer(GravityCompat.START)
                 return true;
             }
        }
        return super.onOptionsItemSelected(item);
    }

     public fun selectDrawerItem( menuItem:MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked

        when(menuItem.getItemId()) {
            R.id.nav_first_fragment-> {
                navigateTo(MapFragment.newInstance(),true)
            }
             R.id.nav_second_fragment-> {
                 navigateTo(UserDataFragment.newInstance(),true)
             }
             R.id.nav_third_fragment-> {
                 navigateTo(BasketFragment.newInstance(),true)
             }
            R.id.nav_fourth_fragment-> {
                navigateTo(OrderHistory.newInstance(),true)
            }

            R.id.nav_fifth_fragment-> {
                navigateTo(EventFragment.newInstance(),true)
            }
            R.id.nav_sixth_fragment-> {
                //ADD LOGOUT HERE
              //  navigateTo(EventFragment.newInstance(),true)
            }
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
         activity_main_drawer_layout.closeDrawers();
    }

    fun addUser(user:User){


        db.collection("users")
            .add(user)
            .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                user.id=documentReference.id
                UserUtils(applicationContext).saveSession(user.id,true,UserType.FIREBASE)

                Log.d(
                    MainActivity.TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            })
            .addOnFailureListener(OnFailureListener { e ->
                Log.w(
                    MainActivity.TAG,
                    "Error adding document",
                    e
                )
            })
    }
    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        activity_main_drawer_layout.addDrawerListener(this)
        if(UserUtils(applicationContext).isLogged()){
            var us:User?=User()

            UserUtils(applicationContext).getUserID()?.let {
                db.collection("users").document(it)
                    .get()
                    .addOnSuccessListener {
                        if(it.exists()){
                            us= it.toObject(User::class.java)
                            updateUI(us, UserUtils(applicationContext).getLogedUserType())

                        }else{
                            updateUI(null,null)
                        }

                    }.addOnFailureListener{
                        Log.d(TAG, "get failed with ", it)
                    }
            }


        }else{

        }
       ;

    }
    // [END on_start_check_user]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                updateUI(null,UserType.GOOGLE)
                // [END_EXCLUDE]
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }
    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        showProgressBar()

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    var us=User()
                    us.email=email;
                    us.password=password
                    addUser(us)
                   // val user = auth.currentUser
                    updateUI(us,UserType.CREATED)


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null,null)
                }

                // [START_EXCLUDE]
                hideProgressBar()
                // [END_EXCLUDE]
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        showProgressBar()

        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = User()
                    user.email=email
                    user.password=password

                    db.collection("users")
                        .whereEqualTo(email,user?.email)
                        .get()
                        .addOnSuccessListener {

                            if (it.isEmpty){
                                addUser(user)
                            }
                        }




                    updateUI(user,UserType.FIREBASE)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null,null)
                }


                hideProgressBar()
                // [END_EXCLUDE]
            }
        // [END sign_in_with_email]
    }

    private fun signInG() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun signOutG() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null,null)
        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        // [START_EXCLUDE silent]
        showProgressBar()
        // [END_EXCLUDE]

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    var us=User()
                    us.email= user?.email.toString();
                    db.collection("users")
                        .whereEqualTo(FieldPath.documentId(),user?.uid)
                        .get()
                        .addOnSuccessListener {

                            if (it.isEmpty){
                                addUser(us)
                            }
                        }


                    updateUI(us,UserType.GOOGLE)
                   // UserUtils(applicationContext).saveSession(acct.id,true,UserType.GOOGLE)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null,null)
                }

                // [START_EXCLUDE]
                hideProgressBar()
                // [END_EXCLUDE]
            }
    }
    private fun signOut() {
        auth.signOut()
        LoginManager.getInstance().logOut()

        updateUI(null,null)
    }


    private fun validateForm(): Boolean {
        var valid = true

        val email = fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            fieldEmail.error = "Required."
            valid = false
        } else {
            fieldEmail.error = null
        }

        val password = fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            fieldPassword.error = "Required."
            valid = false
        } else {
            fieldPassword.error = null
        }

        return valid
    }

    private fun updateUI(user: User?, type: UserType?) {
        hideProgressBar()
        if (user != null) {
            if(type==UserType.FACEBOOK){
                buttonFacebookLogin.visibility = View.GONE
                buttonFacebookSignout.visibility = View.VISIBLE

            }else if(type==UserType.GOOGLE){
                login_google_text.text="Wyloguj się"
            }else if(type==UserType.CREATED){
                emailPasswordButtons.visibility = View.GONE
                emailPasswordFields.visibility = View.GONE
                signedInButtons.visibility = View.VISIBLE

            }else {

            }


            navigateTo(MapFragment.newInstance(),false)



        } else {
            detail.text = null
            buttonFacebookLogin.visibility = View.VISIBLE
            buttonFacebookSignout.visibility = View.GONE

            emailPasswordButtons.visibility = View.VISIBLE
            emailPasswordFields.visibility = View.VISIBLE
            signedInButtons.visibility = View.GONE
            login_google_text.text="Zaloguj się"

        }
    }

    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.emailCreateAccountButton -> createAccount(fieldEmail.text.toString(), fieldPassword.text.toString())
            R.id.emailSignInButton -> signIn(fieldEmail.text.toString(), fieldPassword.text.toString())
            R.id.signOutButton -> signOut()
            R.id.buttonFacebookSignout->signOut()

            R.id.sing_google -> signInG()

          //  R.id.disconnectButton -> revokeAccess()
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        // [START_EXCLUDE silent]
        showProgressBar()
        // [END_EXCLUDE]

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    var us:User?=User()
                    us?.email= user?.email.toString()

                    db.collection("users")
                        .whereEqualTo("email",auth.currentUser?.email)
                        .get()
                        .addOnSuccessListener {

                            if (it.isEmpty){
                                us?.let { it1 -> addUser(it1) }
                            }else{
                                for (document in it.documents) {
                                    Log.d(TAG, "${document.id} => ${document.data}")
                                    UserUtils(applicationContext).saveSession(document.id,true,UserType.FACEBOOK)

                                    updateUI(document.toObject(User::class.java),UserType.FACEBOOK)

                                }
                            }
                        }




                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null,null)
                }

                // [START_EXCLUDE]
                hideProgressBar()
                // [END_EXCLUDE]
            }
    }

    override fun onBackStackChanged() {

    }

    override fun navigateTo(fragment: BaseFragment?, addToBackstack: Boolean): Boolean {
        val manager: FragmentManager? = supportFragmentManager
        // Activity must be initialized and fragment non null to proceed
        if (fragment == null || manager == null) {
            return false
        }
        // Prevent adding same page twice
        val current: Fragment? = manager.findFragmentById(R.id.main_layout)
        if (current != null && fragment.javaClass.equals(current.javaClass)) {
            return false
        }
        // finally make fragment transaction
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(R.id.main_layout, fragment)
        if (addToBackstack) {
            transaction.addToBackStack(fragment.toString())
        }

        transaction.commit()
        return true
    }

    override fun navigateBack(): Boolean {
        onBackPressed()
        return true
    }

    override fun topBar(): TopBarInteractions? {
        return topBar
    }
    companion object {
        private const val TAG = "EmailPassword"
        private const val RC_SIGN_IN = 9001

    }



    override fun onDestroy() {
        super.onDestroy()
        activity_main_drawer_layout.removeDrawerListener(this)
    }

    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerOpened(drawerView: View) {

    }
}

// ...
// Initialize Firebase Auth
