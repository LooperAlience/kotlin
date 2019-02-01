package chela.kotlin.sql

import android.content.ContentResolver
import android.database.CharArrayBuffer
import android.database.ContentObserver
import android.database.Cursor
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle

class ChCursor internal constructor(c: Cursor, isRecord:Boolean): Cursor {
    var msg:String = ""
    val length = c.count
    private val fields = c.columnNames
    private val columeCount = c.columnCount
    private val fieldTypes = c.columnNames.mapIndexed{idx, _->c.getType(idx)}
    private val range = (0 until c.columnCount)
    val rs:Array<Array<Any?>> = Array(if(isRecord) 1 else c.count) ch@{
        if(it == 0) c.moveToFirst()
        val r = range.map {
            when(c.getType(it)){
                Cursor.FIELD_TYPE_INTEGER-> c.getInt(it)
                Cursor.FIELD_TYPE_FLOAT->c.getFloat(it)
                Cursor.FIELD_TYPE_STRING->c.getString(it)
                Cursor.FIELD_TYPE_BLOB->c.getBlob(it)
                else->null
            }
        }.toTypedArray<Any?>()
        c.moveToNext()
        r
    }
    inline fun forEach(block:(Int, Array<Any?>)->Unit) = rs.forEachIndexed(block)
    private var cursor = 0
    private var row:Array<Any?> = rs[0]
    private fun setRow(c: Int): Boolean {
        cursor = c
        row = rs[c]
        return true
    }
    operator fun <T> get(idx: Int, def:T):T{
        @Suppress("UNCHECKED_CAST")
        return if(row[idx] != null) row[idx] as T else def
    }
    override fun getBlob(idx:Int):ByteArray = get(idx, ByteArray(0))
    override fun getDouble(idx:Int):Double = get(idx, 0.0)
    override fun getFloat(idx:Int):Float = get(idx, 0F)
    override fun getInt(idx:Int):Int = get(idx, 0)
    override fun getShort(idx:Int):Short = get(idx, 0)
    override fun getLong(idx: Int):Long = get(idx, 0L)
    override fun getString(idx: Int):String = get(idx, "")
    override fun getColumnCount():Int = columeCount
    override fun getColumnName(columnIndex:Int):String = fields[columnIndex]
    override fun getColumnNames():Array<String> = fields
    override fun getCount():Int = length
    override fun getPosition():Int = cursor
    override fun getType(columnIndex:Int):Int = fieldTypes[columnIndex]
    override fun isAfterLast():Boolean = cursor == length - 2
    override fun isBeforeFirst():Boolean = cursor == 1
    override fun isFirst():Boolean = cursor == 0
    override fun isLast():Boolean = cursor == length - 1
    override fun isNull(columnIndex:Int): Boolean = row[columnIndex] == null
    override fun getColumnIndex(columnName:String):Int = fields.indexOf(columnName)
    @Throws(IllegalArgumentException::class)
    override fun getColumnIndexOrThrow(columnName:String):Int{
        val i = fields.indexOf(columnName)
        if (i == -1) throw IllegalArgumentException(columnName)
        return i
    }
    override fun move(offset:Int):Boolean{
        val i = cursor + offset
        return i < length && setRow(i)
    }
    override fun moveToPosition(position:Int):Boolean = position > -1 && position < length && setRow(position)
    override fun moveToFirst():Boolean = setRow(0)
    override fun moveToLast():Boolean = setRow(length - 1)
    override fun moveToNext():Boolean = cursor < length - 1 && setRow(++cursor)
    override fun moveToPrevious():Boolean = cursor > 0 && setRow(--cursor)
    override fun isClosed():Boolean = true
    override fun getWantsAllOnMoveCalls():Boolean = false
    override fun getNotificationUri(): Uri? = null
    override fun setExtras(ex: Bundle?){}
    override fun getExtras(): Bundle? = null
    override fun respond(extras: Bundle): Bundle? = null
    override fun copyStringToBuffer(columnIndex: Int, buffer: CharArrayBuffer) {}
    override fun close() {}
    override fun registerDataSetObserver(observer: DataSetObserver) {}
    override fun registerContentObserver(observer: ContentObserver) {}
    override fun setNotificationUri(cr: ContentResolver, uri: Uri) {}
    override fun unregisterContentObserver(observer: ContentObserver) {}
    override fun unregisterDataSetObserver(observer: DataSetObserver) {}
    @Deprecated("")
    override fun deactivate() {}
    @Deprecated("")
    override fun requery():Boolean = false
}