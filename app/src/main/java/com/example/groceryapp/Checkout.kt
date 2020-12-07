package com.example.groceryapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.groceryapp.Adapter.MySingleton
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class Checkout : AppCompatActivity() {

    //initialise based on UI
     var paymentMethod:String = "e-wallet"
     var deliveryMethod:String = "Poslaju"

    lateinit var address:EditText
    lateinit var notes:EditText
    lateinit var amount:String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        //get the card_id from a=main activity
        val cartID = intent.getStringExtra("cart_id")
        Log.e("winniecheck",cartID.toString())

        val backButton: ImageButton = findViewById(R.id.back3)
        backButton.setOnClickListener {
            finish()
        }

        //radio group credit: https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        //Delivery Radio Group
        val payment_radio_group:RadioGroup = findViewById(R.id.payment)
        payment_radio_group.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    val radio: RadioButton = findViewById(checkedId)
                    Toast.makeText(applicationContext,"Payment Choice :"+
                            " ${radio.text}",
                            Toast.LENGTH_SHORT).show()
                    paymentMethod= "${radio.text}"
                })

        //Delivery Radio Group
        val delivery_radio_group:RadioGroup = findViewById(R.id.delivery)
        delivery_radio_group.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    val radio: RadioButton = findViewById(checkedId)
                    Toast.makeText(applicationContext,"Delivery Choice :"+
                            " ${radio.text}",
                            Toast.LENGTH_SHORT).show()
                    deliveryMethod= "${radio.text}"
                })

        //Payment Amount Text
        var payment_amount:TextView = findViewById(R.id.amount)
        amount = intent.getStringExtra("amount").toString()
        payment_amount.text = "RM " + amount

        address = findViewById(R.id.address)
        notes= findViewById(R.id.note)

        //When user click on pay button
        val pay:Button = findViewById(R.id.pay)
        pay.setOnClickListener {

            //need to ensure radio button is checked (for payment field and delivery field)
            //delivery address cannot be empty
            if(address.text.isEmpty()){
                address.error = "Delivery Address is required!";
                address.requestFocus();
            }
            else{
                //FLOW:DATABASE PHP
                //Step0: get the order_id
                //Step1: get the user's cart_id
                //Step2: get all the product's in the cart_id
                //Step3: insert all the items in cartitems to the orderitems table
                //Step4: delete all the items in the cartitems

                //FLOW:ANDROID STUDIO
                //order_id, payment_id, delivery_id with relative info need to be write to database
                //order_id is generated by the server, but payment_id and delivery_id is generate by here
                //the cart items have to be clear :)
                //move the cart items to the orderitems table (do at backend)
                //get the current date and time to write to database
                writeOrder()
            }
        }
    }

    //able to write order but can't response to user so far
    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeOrder() {

        //record the date and time when user place order to add to database
        //credit to:https://grokonez.com/kotlin/kotlin-get-current-datetime#I_Kotlin_8211_Get_Current_DateTime
        val currentDateTime = LocalDateTime.now()
        var date:String = currentDateTime.format(DateTimeFormatter.ISO_DATE)
        var time:String = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        //variable to write to server
        //self-generated ID
        var order = (Math.random()*100000000).toInt()
        val order_number:String = String.format("%08d",order)

        var delivery = (Math.random()*100000000).toInt()
        val delivery_id:String = "D" + String.format("%08d",delivery)

        var payment = (Math.random()*100000000).toInt()
        val payment_id:String = "P" + String.format("%08d",payment)


        //user_id that get from intent
        var user_id = 1

        //write order details to database using json
        val info:String = "https://groceryapptarucproject.000webhostapp.com/grocery/order/insertorder.php"+
                "?order_number=$order_number&payment_amount=$amount" + "&payment_method=$paymentMethod&delivery_address=${address.text}&user_id=$user_id&delivery_id=$delivery_id&payment_id=$payment_id&delivery_method=$deliveryMethod&order_date=$date&order_time=$time"
        Log.e("winnie",info)

        Log.e("winnie",amount)
        Log.e("winnie",address.text.toString())

        //BUG (SOLVED)
        //if user did not re-choose the radio button, error will be occured
        //if they re-choose and cause the toast of selection displayed, then there will be no bug :)
       Log.e("winnie",deliveryMethod)
       Log.e("winnie",paymentMethod)

        //this part got error
        //ERROR1: CANT WRITE THE CARTITEMS INTO ORDERITEMS
        val stringRequest = StringRequest(Request.Method.GET, info,
                Response.Listener<String> { response ->
                    Log.e("winnie", response)

                    try{
                        if(response != null){
                            val strResponse = response.toString()
                            val jsonResponse  = JSONObject(strResponse)
                            val success: String = jsonResponse.get("success").toString()

                            if(success == "1"){
                                Toast.makeText(this,"Loading... ", Toast.LENGTH_LONG).show()

                            }else{
                                Toast.makeText(this, "Error occured. Please try again later", Toast.LENGTH_LONG).show()
                            }

                        }
                    }catch (e:Exception){
                        Log.d("Main", "Response: %s".format(e.message.toString()))
                    }

                },
                Response.ErrorListener { error -> Log.d("Main", "Response: %s".format(error.message.toString())) })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)


        //update orderitems, remove cartitems
        //TO DO: TRY TO WRITE TO SERVER
        //SUCCESS WHILE RUNNING ON SERVER
        val url:String =  "https://groceryapptarucproject.000webhostapp.com/grocery/order/insertcartitemsintoorderitems.php" +
                "?order_number=" + order_number  + "&user_id=" + user_id
        Log.e("winnie",url)

        val stringRequest1 = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    Log.e("winnie", response)

                    try{
                        if(response != null){
                            val strResponse = response.toString()
                            val jsonResponse  = JSONObject(strResponse)
                            val success: String = jsonResponse.get("success").toString()

                            if(success == "1"){
                                Toast.makeText(this,"Order placed successfully.", Toast.LENGTH_LONG).show()

                            }else{
                                Toast.makeText(this, "Error occured. Please try again later", Toast.LENGTH_LONG).show()
                            }

                        }
                    }catch (e:Exception){
                        Log.d("Main", "Response: %s".format(e.message.toString()))
                    }

                },
                Response.ErrorListener { error -> Log.d("Main", "Response: %s".format(error.message.toString())) })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest1)

    }
}