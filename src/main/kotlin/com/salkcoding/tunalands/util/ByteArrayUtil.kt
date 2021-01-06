package com.salkcoding.tunalands.util

import java.io.ByteArrayInputStream
import java.io.DataInputStream

fun ByteArray.toDataInputStream(): DataInputStream {
    return DataInputStream(ByteArrayInputStream(this))
}