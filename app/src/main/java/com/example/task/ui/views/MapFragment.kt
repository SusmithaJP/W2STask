package com.example.task.ui.views

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.task.R
import com.example.task.data.model.DirectionResponses
import com.example.task.data.repository.MainRepository
import com.example.task.data.repository.RetrofitService
import com.example.task.ui.TaskViewModelFactory
import com.example.task.ui.viewmodel.MapViewModel
import com.example.task.utils.LocationUtil
import com.example.task.utils.Status
import com.example.task.utils.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.IOException
import com.google.maps.android.PolyUtil


class MapFragment : Fragment(), GoogleMap.OnMarkerClickListener,
    LocationUtil.MyLocationListener, View.OnClickListener {

    private lateinit var mMap: GoogleMap;
    private lateinit var mTvFmSourceLocation: TextView
    private lateinit var mEtFmDestinationLocation: EditText
    private lateinit var mBtnShowDirection: Button
    private lateinit var mIvFmSearchDestination: ImageButton
    private lateinit var mLlFmLoaderContainer: LinearLayout
    private lateinit var mSourceLatLan: LatLng
    private lateinit var mDestinationLatLan: LatLng
    private var mSupportMapFragment: SupportMapFragment? = null
    private val mRetrofitMapService = RetrofitService.getMApInstance()
    private lateinit var mViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        setUI(view)
        setupViewModel()
        return view
    }

    private fun setUI(view: View) {
        mTvFmSourceLocation = view.findViewById<TextView>(R.id.tvFmSourceLocation)
        mEtFmDestinationLocation = view.findViewById<EditText>(R.id.etFmDestinationLocation)
        mBtnShowDirection = view.findViewById<Button>(R.id.btnFmShowDirection)
        mIvFmSearchDestination = view.findViewById<ImageButton>(R.id.ivFmSearchDestination)
        mLlFmLoaderContainer = view.findViewById<LinearLayout>(R.id.llFmLoaderContainer)
        mIvFmSearchDestination.setOnClickListener(this)
        mBtnShowDirection.setOnClickListener(this)
        if (mSupportMapFragment == null) {
            val fragmentManager: FragmentManager? = fragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
            mSupportMapFragment = SupportMapFragment.newInstance()
            val supportMapFragment = mSupportMapFragment
            if (supportMapFragment != null) {
                fragmentTransaction.replace(R.id.fragFmMapContainer, supportMapFragment).commit()
            }
        }
        if (mSupportMapFragment != null) {
            mSupportMapFragment?.getMapAsync {
                mMap = it
                mMap.uiSettings.isZoomControlsEnabled = true
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL)
                mMap.getUiSettings().setMyLocationButtonEnabled(true)
                mMap.setOnMarkerClickListener(this)
                context?.let { context ->
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLlFmLoaderContainer.visibility = View.VISIBLE
                    LocationUtil().startListeningUserLocation(context, this)
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LocationUtil.LOCATION_PERMISSION_REQUEST_CODE)
                }
                }
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng, isSourceLatlan: Boolean) {
        if ((!::mMap.isInitialized) || TextUtils.isEmpty(location.latitude.toString()) || TextUtils.isEmpty(
                location.longitude.toString()
            )
        )
            return
        val markerOptions = MarkerOptions().position(location)
        val titleStr = getAddress(location)  // add these two lines
        if (isSourceLatlan) {
            mTvFmSourceLocation.setText(titleStr)
            markerOptions.icon(Util.bitmapDescriptorFromVector(context, R.drawable.source_map_pin))
        } else{
            markerOptions.icon(Util.bitmapDescriptorFromVector(context, R.drawable.map_pin))
        }
        markerOptions.title(titleStr)
        mMap.addMarker(markerOptions).showInfoWindow()
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 8f))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((context?.let {
                            ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                        context?.let { it1 -> LocationUtil().createLocationRequest(it1, this) }
                    }
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(activity)
        val addressList: List<Address>?
        val address: Address?
        var addressText = ""
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addressList && !addressList.isEmpty()) {
                address = addressList.get(0)
                val sb = StringBuilder()
                if(address.maxAddressLineIndex > 0){
                    for (i in 0 until address.maxAddressLineIndex) {
                        sb.append(address.getAddressLine(i)).append("\n")
                    }
                    sb.append(address.subLocality).append(",")
                    sb.append(address.locality).append(",")
                    sb.append(address.postalCode).append(",")
                    sb.append(address.countryName)
                } else{
                    sb.append(address.subLocality).append(",")
                    sb.append(address.locality).append(",")
                    sb.append(address.postalCode).append(",")
                    sb.append(address.countryName)
                }
                addressText = sb.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MapFragment", e.localizedMessage)
        }

        return addressText
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker != null) {
            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
            } else {
                marker.showInfoWindow();
            }
        }
        return true; }

    override fun onLocationChanged(location: Location) {
        if (location != null) {
            val currentLatLng = LatLng(location.latitude, location.longitude)
            mSourceLatLan = currentLatLng
            placeMarkerOnMap(currentLatLng, true)
            mLlFmLoaderContainer.visibility = View.GONE
        }
    }

    fun searchLocation(locationSearch: EditText) {
        mLlFmLoaderContainer.visibility = View.VISIBLE
        lateinit var location: String
        location = locationSearch.text.toString()
        var addressList: List<Address>? = null

        if (TextUtils.isEmpty(location)) {
            mLlFmLoaderContainer.visibility = View.GONE
            Toast.makeText(context, "Provide location", Toast.LENGTH_SHORT).show()
        } else {
            val geoCoder = Geocoder(context)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
                if (null != addressList && !addressList.isEmpty()) {
                    val address = addressList[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    mDestinationLatLan = latLng
                    placeMarkerOnMap(mDestinationLatLan, false)
                    mLlFmLoaderContainer.visibility = View.GONE
                } else{
                    Toast.makeText(context, "Provide correct location", Toast.LENGTH_SHORT).show()
                    mLlFmLoaderContainer.visibility = View.GONE
                }
            } catch (e: IOException) {
                mLlFmLoaderContainer.visibility = View.GONE
                e.printStackTrace()
            }

        }
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, TaskViewModelFactory(MainRepository(mRetrofitMapService))).get(
                MapViewModel::class.java
            )
    }
    fun showDirection (){
        mLlFmLoaderContainer.visibility = View.VISIBLE
        if((!::mDestinationLatLan.isInitialized) || (!::mSourceLatLan.isInitialized)|| TextUtils.isEmpty(mSourceLatLan.latitude.toString()) || TextUtils.isEmpty(mSourceLatLan.latitude.toString())||
            TextUtils.isEmpty(mDestinationLatLan.latitude.toString()) || TextUtils.isEmpty(mDestinationLatLan.latitude.toString())){
            Toast.makeText(context, "Check your source and destination location is correct!", Toast.LENGTH_LONG)
            mLlFmLoaderContainer.visibility = View.GONE
            return
        }
        val source = mSourceLatLan.latitude.toString() + "," + mSourceLatLan.longitude.toString()
        val destination = mDestinationLatLan.latitude.toString() + "," + mDestinationLatLan.longitude.toString()
        mViewModel.fetchData(source, destination, getString(R.string.google_map_api_key))
        mViewModel.getDirectionRoutes().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    mLlFmLoaderContainer.visibility = View.GONE
                    it.data?.let { directionData -> drawPolyline(directionData) }
                }
                Status.LOADING -> {
                    mLlFmLoaderContainer.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    mLlFmLoaderContainer.visibility = View.GONE
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun drawPolyline(response: DirectionResponses) {
        if (response.status.equals(getString(R.string.request_denied))){
            Toast.makeText(context, "Request denied!!", Toast.LENGTH_LONG)
            return
        }
        if (response.routes?.size!! > 0){
            val shape = response.routes?.get(0)?.overviewPolyline?.points
            val polyline = PolylineOptions()
                .addAll(PolyUtil.decode(shape))
                .width(8f)
                .color(Color.RED)
            mMap.addPolyline(polyline)
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.ivFmSearchDestination->{
                searchLocation(mEtFmDestinationLocation)
                return
            }
            R.id.btnFmShowDirection->{
                showDirection()
                return
            }
        }
    }

}