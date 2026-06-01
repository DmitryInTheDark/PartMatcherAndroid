package com.app.partmatcher

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.databinding.ActivityMainBinding
import com.app.partmatcher.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var currentMenuRes: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val tokenManager = TokenManager(this)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.bottomNav.visibility = View.GONE
                    currentMenuRes = null // Reset menu state when logging out/in
                }
                else -> {
                    val token = tokenManager.getToken()
                    if (token != null) {
                        binding.bottomNav.visibility = View.VISIBLE
                        val roles = tokenManager.getRoles()
                        if (roles.isNotEmpty()) {
                            setupMenuForRole(roles)
                        } else {
                            fetchUserRoleAndSetupNav()
                        }
                    } else {
                        binding.bottomNav.visibility = View.GONE
                    }
                }
            }
        }

        if (tokenManager.getToken() == null) {
            navGraph.setStartDestination(startDestId = R.id.loginFragment)
        } else {
            val roles = tokenManager.getRoles()
            val startDest = when {
                roles.contains("ADMIN") || roles.contains("ROLE_ADMIN") -> R.id.adminStatsFragment
                roles.contains("SUPPORT") || roles.contains("ROLE_SUPPORT") -> R.id.chatFragment
                else -> R.id.homeFragment
            }
            navGraph.setStartDestination(startDestId = startDest)
            setupMenuForRole(roles)
            fetchUserRoleAndSetupNav()
        }
        navController.graph = navGraph

        binding.bottomNav.setupWithNavController(navController)
    }

    private fun fetchUserRoleAndSetupNav() {
        NetworkModule.getApiService(this).getMe().enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val tokenManager = TokenManager(this@MainActivity)
                        tokenManager.saveUser(user.id, user.name, user.roles)
                        setupMenuForRole(user.roles)
                    }
                } else if (response.code() == 401) {
                    TokenManager(this@MainActivity).clearToken()
                    navController.navigate(resId = R.id.loginFragment)
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                // If network fails, we can't determine role
            }
        })
    }

    private fun setupMenuForRole(roles: Set<String>) {
        val menuRes = when {
            roles.contains("ADMIN") || roles.contains("ROLE_ADMIN") -> R.menu.bottom_nav_menu_admin
            roles.contains("SUPPORT") || roles.contains("ROLE_SUPPORT") -> R.menu.bottom_nav_menu_support
            else -> R.menu.bottom_nav_menu_user
        }

        if (currentMenuRes == menuRes) return
        currentMenuRes = menuRes

        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(menuRes)
        binding.bottomNav.setupWithNavController(navController)

        // If user is Admin, ensure they are not stuck on the User Home screen if they just logged in/started
        if (roles.contains("ADMIN") || roles.contains("ROLE_ADMIN")) {
            if (navController.currentDestination?.id == R.id.homeFragment) {
                navController.navigate(R.id.adminStatsFragment)
            }
        }
    }
}
