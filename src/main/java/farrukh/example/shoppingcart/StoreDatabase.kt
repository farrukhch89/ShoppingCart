package farrukh.example.shoppingcart

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.SQLException


class StoreDatabase(private val mContext: Context) {
    private lateinit var mDbHelper: DatabaseHelper
    private lateinit var mDb: SQLiteDatabase

    private class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            Log.w(TAG, DATABASE_CREATE)
            db.execSQL(DATABASE_CREATE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + SHOP_TABLE)
            onCreate(db)
        }
    }

    @Throws(SQLException::class)
    fun open(): StoreDatabase {
        mDbHelper = DatabaseHelper(mContext)
        mDb = mDbHelper!!.writableDatabase
        return this
    }

    fun close() {
        if (mDbHelper != null) {
            mDbHelper!!.close()
        }
    }

    fun createItem(name: String?, description: String?, price: Int, status: String): Long {
        val initialValues = ContentValues()
        initialValues.put(KEY_NAME, name)
        initialValues.put(KEY_DESCRIPTION, description)
        initialValues.put(KEY_PRICE, price)
        initialValues.put(KEY_STATUS, status)
        return mDb!!.insert(SHOP_TABLE, null, initialValues)
    }

    // delete all item in list
    fun deleteAllItems(): Boolean {
        var doneDelete = 0
        doneDelete = mDb!!.delete(SHOP_TABLE, null, null)
        Log.w(TAG, Integer.toString(doneDelete))
        return doneDelete > 0
    }

    // use to count the row in cart to loop and find items
    fun getCartItemsRowCount(type: Int): Int {
        mDb = mDbHelper!!.readableDatabase
        return DatabaseUtils.queryNumEntries(
            mDb,
            SHOP_TABLE,
            "status= ? ",
            arrayOf(Integer.toString(type))
        )
            .toInt()
    }

    // add to cart method
    fun addToCart(id: Int?, `val`: String?): Boolean {
        mDb = mDbHelper!!.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_STATUS, `val`)
        val update = this.mDb.update(
            SHOP_TABLE, contentValues, "_id= ? ", arrayOf(
                Integer.toString(
                    id!!
                )
            )
        )
        mDb.close()
        return true
    }

    // get total price of cart
    fun getAmount(): Int {
        mDb = mDbHelper.readableDatabase
        val cursor = mDb.rawQuery(
            "SELECT SUM(" + KEY_PRICE + ") FROM " + SHOP_TABLE + " WHERE status=1",
            null
        )
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        return total
    }

    // bring all item from database
    fun fetchAllItems(status: Int): Cursor? {
        val mCursor = mDb!!.query(
            SHOP_TABLE, arrayOf(KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_PRICE, KEY_STATUS),
            KEY_STATUS + " like '%" + status + "%'", null, null, null, null, null
        )
        mCursor?.moveToFirst()
        return mCursor
    }

    // add item in database
    fun insertMyShopItems() {
        createItem(
            "Headphones",
            """
                Noise Cancelling Headphones with Multiple Modes
                Comfortable Fit, Bluetooth Headphones, Connect to 2 Devices
                
                """.trimIndent(), 7000, "6"
        )
        createItem(
            "Bluetooth Speaker",
            "Incredible Sound Quality The stereo active dual Bluetooth speaker combination creates an optimized sound filed for dynamic range and exceptional clarity across all music. True 360 degree surround sound, light up your backyard BBQ Feast, Birthday Party, Family Gathering, Holiday’s atmosphere on top of the world.  ",
            5600,
            "0"
        )
        createItem(
            "Echo Dot (3rd Gen) - Smart speaker with Alexa ",
            "Voice control your music: stream songs from Amazon Music, Apple Music, Spotify, TuneIn and others. You can also listen to audiobooks from Audible",
            3000,
            "0"
        )
        createItem(
            "Fire 7 Tablet",
            "7\" IPS display, 16 or 32 GB of internal storage (up to 512 GB of expandable storage with microSD).",
            7000,
            "0"
        )
        createItem(
            "SanDisk Ultra 32 GB USB Flash",
            "SanDisk Flash USB, 3.0 USB, 100 Megabytes per second speed",
            800,
            "0"
        )
        createItem(
            "Apexcam 4K Action Camera 20MP 40M",
            "Camera provides professional 4k/30fps video, up to 30 frames per second. The built-in anti-shake function and equipped with 6 layers of optical glass lenses can effectively eliminate vibration, and at the same time, it can bring you vivid and realistic images to ensure that pictures and videos are more clear and smooth.",
            7500,
            "0"
        )
        createItem(
            "Kindle E-Reader",
            " Purpose built for reading, with a 167 ppi glare-free display that reads like printed paper, even in direct sunlight",
            7500,
            "0"
        )
        createItem(
            "Portable Monitor, ARZOPA 15.6 Inch, 1920×1080 FHD, 100% SRGB",
            "ARZOPA portable monitor has Full HD 1920 x 1080 resolution. IPS screen delivers 178° full view angle. 1000:1 contrast ratio, 72% NTSC colour gamut, offer the accurate and vivid image. 16:9 screen proportion and 60HZ refresh rate show the real image of the game/scene/work perfectly and quickly. Blue light filter technology keep your eyes from tired. Automatically turn on eye-care mode in low light environment.",
            13075,
            "0"
        )
        createItem(
            "USB 3.0 Docking Station",
            "HDMI Splitter Extended Display, USB to Dual HDMI Adapter, USB C Laptop Docking Station with 2 HDMI Dual Monitor Adapter Compatible with MacBook Pro/Air M1, Surface Pro, Dell",
            1500,
            "0"
        )
        createItem(
            "Apple MagSafe Charger",
            "Charge faster and easier with MagSafe wireless chargers that align perfectly every time. And choose from a beautiful range of cases and wallets that snap on, just like that. When you attach a MagSafe accessory, iPhone recognises it instantly, for an effortless connection to your device.",
            2800,
            "0"
        )
        createItem(
            "Phone Stand",
            "Sturdy aluminum alloy, smooth edge, sturdy, lightweight, portable, cool metal phone stand matching iphone and smart phones.",
            1500,
            "0"
        )
    }

    // get total of all item in cart
    fun getTotalItemsCount(): Int {
        val countQuery = "SELECT  * FROM " + SHOP_TABLE
        mDb = mDbHelper.readableDatabase
        val cursor = mDb.rawQuery(countQuery, null)
        val cnt = cursor.count
        cursor.close()
        return cnt

    }


    companion object {
        const val KEY_ID = "_id"
        const val KEY_NAME = "name"
        const val KEY_DESCRIPTION = "description"
        const val KEY_PRICE = "price"
        const val KEY_STATUS = "status"
        private const val TAG = "ShopDbAdapter"
        private const val DATABASE_NAME = "PhoenixShop"
        private const val SHOP_TABLE = "shop"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_CREATE = ("CREATE TABLE IF NOT EXISTS "
                + SHOP_TABLE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_NAME + " TEXT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_PRICE + " INTEGER," +
                KEY_STATUS + " String" + ");")

        fun getTotalItemsCount(storeDatabase: StoreDatabase): Int {
            val countQuery = "SELECT  * FROM " + SHOP_TABLE
            storeDatabase.mDb = storeDatabase.mDbHelper.readableDatabase
            val cursor = storeDatabase.mDb.rawQuery(countQuery, null)
            val cnt = cursor.count
            cursor.close()
            return cnt
        }
    }
}
