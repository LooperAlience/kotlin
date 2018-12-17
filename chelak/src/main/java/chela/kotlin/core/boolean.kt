package chela.kotlin.core

inline fun Boolean._true(block:()->Unit){if(this) block()}
inline fun Boolean._false(block:()->Unit){if(!this) block()}