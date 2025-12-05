package com.example.premiere

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import java.util.Calendar
import android.app.DatePickerDialog
import android.widget.CheckBox
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    var currentImageIndex = 0
    lateinit var carouselImage: ImageView
    val handler = Handler(Looper.getMainLooper())

    val images = listOf(
        R.drawable.avatar_face,
        R.drawable.avatar_closeup,
        R.drawable.avatar_fly
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        carouselImage = findViewById(R.id.poster_carousel)
        startCarousel()


        var ticketPrice = 15
        var currentQuantity = 0
        var is3DSelected = false
        val price3D = 3

        val ticketsSeekBar = findViewById<SeekBar>(R.id.tickets_seekbar)
        val amountTextView = findViewById<TextView>(R.id.tickets_amount_text)
        val totalPriceTextView = findViewById<TextView>(R.id.total_price_text)
        val ticketTypeGroup = findViewById<RadioGroup>(R.id.ticket_type_radio_group)
        val checkbox3D = findViewById<CheckBox>(R.id.checkbox_3d)
        val dateButton = findViewById<Button>(R.id.date_picker_button)
        val dateTextView = findViewById<TextView>(R.id.selected_date_text)


        totalPriceTextView.text = getString(R.string.total_price_format, 0)


        fun calculateAndUpdatePrice() {
            var total = currentQuantity * ticketPrice
            if (is3DSelected) {
                total += (currentQuantity * price3D)
            }
            totalPriceTextView.text = getString(R.string.total_price_format, total)
        }

        ticketTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_adult) ticketPrice = 15
            else if (checkedId == R.id.radio_child) ticketPrice = 10
            calculateAndUpdatePrice()
        }


        ticketsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentQuantity = progress
                amountTextView.text = progress.toString()
                calculateAndUpdatePrice()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        checkbox3D.setOnCheckedChangeListener { _, isChecked ->
            is3DSelected = isChecked
            calculateAndUpdatePrice()
        }


        dateButton.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val datePickerDialog = android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    dateTextView.text = "$day/${month + 1}/$year"
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        val getTicketsBtn = findViewById<Button>(R.id.get_tickets_btn)
        val cinemaInput = findViewById<EditText>(R.id.cinema_input)

        getTicketsBtn.setOnClickListener {


            if (currentQuantity == 0) {
                android.widget.Toast.makeText(this, getString(R.string.order_error), android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val movieName = getString(R.string.movie_name)
            val date = dateTextView.text.toString()
            val cinema = cinemaInput.text.toString().ifEmpty { "Not Specified" }
            val type = if (ticketPrice == 15) getString(R.string.adult) else getString(R.string.child)
            val extras = if (is3DSelected) getString(R.string.checkbox_3d) else "Normal"
            val currentTotal = (currentQuantity * ticketPrice) + (if(is3DSelected) currentQuantity * price3D else 0)


            val orderDetails = getString(R.string.order_details_template,
                movieName, date, cinema, currentQuantity, type, extras, currentTotal)


            val dialogView = layoutInflater.inflate(R.layout.dialog_checkout, null)

            val builder = android.app.AlertDialog.Builder(this)
            builder.setView(dialogView)
            builder.setCancelable(false)
            val customDialog = builder.create()


            val detailsText = dialogView.findViewById<TextView>(R.id.dialog_order_details)
            val confirmBtn = dialogView.findViewById<Button>(R.id.dialog_confirm_btn)
            val cancelBtn = dialogView.findViewById<Button>(R.id.dialog_cancel_btn)


            detailsText.text = orderDetails


            confirmBtn.setOnClickListener {
                android.widget.Toast.makeText(this, getString(R.string.order_success), android.widget.Toast.LENGTH_LONG).show()
                customDialog.dismiss()
            }

            cancelBtn.setOnClickListener {
                customDialog.dismiss()
            }


            customDialog.show()
        }
    }

    private fun startCarousel() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                currentImageIndex = (currentImageIndex + 1) % images.size

                val fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                carouselImage.setImageResource(images[currentImageIndex])
                carouselImage.startAnimation(fadeIn)

                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }
}