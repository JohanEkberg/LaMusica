package se.westpay.lamusica

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import se.westpay.lamusica.audio.MusicPlayer
import se.westpay.lamusica.databinding.ActivityMainBinding
import se.westpay.lamusica.databinding.AppBarMainBinding
import se.westpay.lamusica.databinding.BottomSheetPlayer2Binding
import se.westpay.lamusica.ui.category.CategoryFragment
import se.westpay.lamusica.ui.categoryalbum.AlbumFragment
import se.westpay.lamusica.ui.categoryartist.ArtistFragment
import se.westpay.lamusica.ui.lyrics.LyricsFragment
import se.westpay.lamusica.ui.player.IPlayerBottomSheet
import se.westpay.lamusica.ui.player.PlayerBottomSheet
import se.westpay.lamusica.ui.search.SearchFragment
import se.westpay.lamusica.ui.settings.SettingsFragment
import se.westpay.lamusica.ui.startup.StartupFragment


class MainActivity : AppCompatActivity(), IPlayerBottomSheet {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var _playerBottomSheet: PlayerBottomSheet
    private lateinit var _binding: ActivityMainBinding
    private lateinit var _bottomSheetBinding: BottomSheetPlayer2Binding
    private var _permissionGrantedArray = booleanArrayOf(false, false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Create bottom sheet binding
            _binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(_binding.root)
            val appBarBinding = AppBarMainBinding.bind(_binding.includeAppBar.root)
            _bottomSheetBinding = BottomSheetPlayer2Binding.bind(appBarBinding.bottomSheetPlayer.root)

            // Create bottom sheet player instance
            _playerBottomSheet = PlayerBottomSheet(_bottomSheetBinding.bottomSheetPLayer2, this)
            _playerBottomSheet.setupListener()

            _playerBottomSheet.lyricsData.observe(this@MainActivity) { lyrics ->
                if (lyrics != null) {
                    val bundle = bundleOf(
                        LyricsFragment.ARG_PARAM_PLAIN_LYRICS to lyrics.plainLyrics,
                        LyricsFragment.ARG_PARAM_SYNCED_LYRICS to lyrics.syncedLyrics,
                        LyricsFragment.ARG_PARAM_DURATION to lyrics.duration
                    )
                    loadFragment(LyricsFragment.FRAGMENT_NAME, bundle)
                } else {
                    Toast.makeText(this@MainActivity,
                        "No lyric was found for current song!",
                        Toast.LENGTH_SHORT).show()
                }
            }

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            val navView: NavigationView = findViewById(R.id.nav_view)
            val navController = findNavController(R.id.nav_host_fragment)
            navView.setupWithNavController(navController)

            toolbar.setNavigationOnClickListener { view ->
                Log.i(TAG, "Toolbar click listener")
            }

            //checkExternalFilesPermission()
            checkPermissions()
        } catch(e: Exception) {
            Log.e(TAG, "Exception in onCreate, exception: ${e.message}")
        }
    }

    private fun isExternalStorageAvailable() : Boolean {
        return Environment.isExternalStorageManager()
    }

    /**
     * Method for obtaining access to external storage.
     *
     */
    private fun checkExternalFilesPermission() {
        Log.i(TAG,"Permission request for external files access...")

        // Register a activity result callback
        val storagePermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult?> { _ ->
                if (!checkAccessToExternalFiles()) {
                    Log.i(TAG,"Access to external files not available")
                }
            }
        )

        if (!checkAccessToExternalFiles()) {
            val uri: android.net.Uri = android.net.Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
            storagePermissionResultLauncher.launch(intent)
        }
    }

    /**
     * Method to check if access to external storage is available.
     *
     * @return Boolean - True access is allowed, false otherwise.
     */
    private fun checkAccessToExternalFiles() : Boolean {
        return try {
            if (!isExternalStorageAvailable()) {
                false
            }  else {
                _permissionGrantedArray[1] = true
                if (_permissionGrantedArray.all { it }) {
                    Log.i(TAG,"All required permissions are granted")
                    startApplication()
                }
                true
            }
        } catch(e: Exception) {
            Log.e(TAG,"Exception: ${e.message}, failed to request all files access")
            false
        }
    }

    /**
     * Method for obtaining runtime permissions.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions() {
        Log.i(TAG,"Permission request for network access...")
        /**
         * Contract for obtaining runtime permissions. If permissions are granted proceeds the application updater events,
         * else logs the error message.
         */
        val runtimePermissionsContract = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isPermissionsGranted = true
            permissions.entries.forEach { permission ->
                isPermissionsGranted = isPermissionsGranted && permission.value
            }
            if (! isPermissionsGranted) {
                Log.e(TAG,"Required permissions not granted, not possible to launch the application")
            }
            else {
                Log.i(TAG,"All permissions granted")
                startApplication()
//                _permissionGrantedArray[0] = true
//                if (_permissionGrantedArray.all { it }) {
//                    Log.i(TAG,"All required permissions are granted")
//                    startApplication()
//                }
            }
        }
        runtimePermissionsContract.launch(arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
    }

    override fun onResume() {
        super.onResume()
        if (_playerBottomSheet.shoudBeVisible()) {
            _playerBottomSheet.show(this@MainActivity)
        } else {
            _playerBottomSheet.hide()
        }
    }

    fun showPlayerBottomSheet() {
        _playerBottomSheet.show(this@MainActivity)
    }

    /**
     * Try to find provided fragment, requirement is that it needs to be visible.
     */
    fun getFirstVisibleFragment(fragmentNames: List<String>) : Fragment? {
        val fragments = this.supportFragmentManager.fragments
        fragments.let { fragmentsNotNull ->
            for (fragment in fragmentsNotNull) {
                if (fragmentNames.contains(fragment::class.simpleName) && fragment.isVisible) {
                    return fragment
                } else {
                    for (childFragment in fragment.childFragmentManager.fragments) {
                        if (fragmentNames.contains(childFragment::class.simpleName) && childFragment.isVisible) {
                            return childFragment
                        }
                    }
                }
            }
        }
        return null
    }

    /**
     * Get fragment id from fragment name.
     */
    private fun getFragmentId(fragmentName: String) : Int {
        return when(fragmentName) {
            AlbumFragment.FRAGMENT_NAME -> R.id.nav_album_songs_fragment
            ArtistFragment.FRAGMENT_NAME -> R.id.nav_artist_fragment
            StartupFragment.FRAGMENT_NAME -> R.id.nav_startup_fragment
            CategoryFragment.FRAGMENT_NAME -> R.id.nav_category_fragment
            SettingsFragment.FRAGMENT_NAME -> R.id.nav_settings_fragment
            SearchFragment.FRAGMENT_NAME -> R.id.nav_search_fragment
            LyricsFragment.FRAGMENT_NAME -> R.id.nav_lyrics_fragment
            else -> -1
        }
    }

    /**
     * Load fragment.
     */
    fun loadFragment(fragmentName: String, argument: Bundle? = null ) {
        try {
            Log.i(TAG, "Load fragment: ${fragmentName}")
            val navController = findNavController(R.id.nav_host_fragment)
            val fragmentId = getFragmentId(fragmentName)
            if (fragmentId != -1) {
                navController.navigate(fragmentId, argument)
            }
        } catch(e: Exception) {
            Log.e(TAG, "Failed to load fragment ${fragmentName}. Exception ${e.message}")
        }
    }

    private fun getCurrentFragment() : Fragment? {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_search)

        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Override onQueryTextSubmit method which is call when submit query is searched
            override fun onQueryTextSubmit(query: String): Boolean {
                // If the list contains the search query than filter the adapter
                // using the filter method with the query as its argument
                val searchFragment = getCurrentFragment() as SearchFragment
                searchFragment.setSearchString(query)
                return false
            }

            // This method is overridden to filter the adapter according
            // to a search query when the user is typing search
            override fun onQueryTextChange(newText: String): Boolean {
                val currentFragment = getCurrentFragment()
                if (currentFragment is SearchFragment) {
                    currentFragment.setSearchString(newText)
                }
                return false
            }
        })

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                this@MainActivity.loadFragment(CategoryFragment.FRAGMENT_NAME)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                this@MainActivity.loadFragment(SettingsFragment.FRAGMENT_NAME)
                true
            }
            R.id.action_search -> {
                this@MainActivity.loadFragment(SearchFragment.FRAGMENT_NAME)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.i(TAG, "onBackPressed")
    }

    private fun startApplication() {
        val startupFragment = getCurrentFragment() as StartupFragment
        startupFragment.startApplication()
    }

    /**
     * Arbitrary id for matching the permission request and response.
     */
    var PERMISSION_REQUEST_CODE = 2000


    override fun onSongChanged(artist: String, title: String, albumUri: Uri?) {
        try {
            if (albumUri == null) {
                Toast.makeText(this@MainActivity, "Queue is empty!", Toast.LENGTH_LONG).show()

                // Change to play button
                _bottomSheetBinding.playerPlayButton.setImageResource(R.drawable.ic_play_circle)
                _bottomSheetBinding.playerPlayButton.tag = 0

                // Update views according to default
                _bottomSheetBinding.artistTextView.text = this@MainActivity.resources.getString(R.string.bottomsheet_artist)
                _bottomSheetBinding.songTextView.text = this@MainActivity.resources.getString(R.string.bottomsheet_song)
                _bottomSheetBinding.albumImageView.setImageDrawable(ResourcesCompat.getDrawable(this@MainActivity.resources, R.drawable.default_music2, null))
                _bottomSheetBinding.progressBar.progress = 0

                // Hide the player
                _playerBottomSheet.hide()
            } else {
                // Update views according to the new song
                _bottomSheetBinding.artistTextView.text = artist
                _bottomSheetBinding.songTextView.text = title

                val artWork = _playerBottomSheet.getAlbumArtwork(this@MainActivity.contentResolver, albumUri)
                _bottomSheetBinding.albumImageView.setImageBitmap(artWork)

                // Reset progress bar
                _bottomSheetBinding.progressBar.progress = 0
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in callback onSongChanged, exception: ${e.message}")
        }
    }

    override fun onSongProgress(progress: Int) {
        try {
            if (!MusicPlayer.isActive()) {
                _bottomSheetBinding.progressBar.progress = 0
            } else {
                getFirstVisibleFragment(listOf(LyricsFragment.FRAGMENT_NAME))?.let {
                    if (it is LyricsFragment) {
                        it.setLyricTimeSpan(progress)
                    }
                }
                _bottomSheetBinding.progressBar.progress = progress
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in callback onSongProgress, exception: ${e.message}")
        }
    }


//    /**
//     * Check and request permission.
//     */
//    private fun hasPermissions(): Boolean {
//        var result = true
//        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
//        if (ContextCompat.checkSelfPermission(
//                        this,
//                        permission
//                ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Set runtime permissions
//            val permissions = arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
//            result = false
//        }
//        return result
//    }

//    /**
//     * Callback for the result from requesting permissions. This method
//     * is invoked for every call on [.requestPermissions].
//     * @param requestCode The request code passed in [.requestPermissions].
//     * @param permissions The requested permissions. Never null.
//     * @param grantResults The grant results for the corresponding permissions
//     * which is either [android.content.pm.PackageManager.PERMISSION_GRANTED]
//     * or [android.content.pm.PackageManager.PERMISSION_DENIED]. Never null.
//     *
//     * @see .requestPermissions
//     */
//    override fun onRequestPermissionsResult(
//            requestCode: Int,
//            permissions: Array<String>,
//            grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.size > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
//            ) {
//              startApplication()
//            } else {
//                Log.e(TAG, "Permissions denied")
//            }
//        }
//    }
}