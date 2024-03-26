package org.bitcoin.kit.key

import fr.acinq.bitcoin.PrivateKey
import fr.acinq.secp256k1.Hex


fun main() {

    // กำหนด private key ในรูปแบบ hex
    val privateKeyHex = "58badd9b455145b487ad24c7f259cc437cc4ffd0784716845249321fb5961cfc"

    // แปลง private key จาก hex เป็น byte array
    val privateKeyBytes = Hex.decode(privateKeyHex)

    // สร้าง private key object
    val privateKey = PrivateKey(privateKeyBytes)

    // ดึง public key จาก private key
    val publicKey = privateKey.publicKey()

    // แสดง public key
    println("Public Key: ${publicKey.toHex()}")
}