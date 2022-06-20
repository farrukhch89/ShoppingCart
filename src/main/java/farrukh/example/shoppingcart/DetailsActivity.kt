package farrukh.example.shoppingcart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.math.BigDecimal
import java.sql.SQLException
import androidx.appcompat.app.AppCompatActivity


class DetailsActivity : AppCompatActivity() {
    private var dbHelper: StoreDatabase? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val bundle: Bundle? = getIntent().getExtras()
        val name = findViewById(R.id.name) as TextView?
        val description = findViewById(R.id.description) as TextView?
        val price = findViewById(R.id.price) as TextView?
        assert(name != null)
        if (bundle != null) {
            name!!.text = bundle.getString("name")
        }
        assert(description != null)
        if (bundle != null) {
            description!!.text = bundle.getString("description")
        }
        val priceVal = bundle?.getInt("price")?.let {
            BigDecimal.valueOf(
                it.toLong(),
                2
            )
        } // we had stored price as a whole integer to include cents e.g 1.00 was stored as 100
        assert(price != null)
        price!!.text = "Price: €$priceVal"
        dbHelper = StoreDatabase(this)
        try {
            dbHelper!!.open()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        val button = (findViewById(R.id.buy) as Button?)!!
        button.setOnClickListener {
            if (bundle != null) {
                if (dbHelper!!.addToCart(bundle.getInt("_id"), "1")) {
                    val intent = Intent(this@DetailsActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(
                        this@DetailsActivity,
                        "Successfully added to shopping cart",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@DetailsActivity,
                        "Oops! Something went wrong. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}