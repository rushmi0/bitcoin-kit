package org.bitcoin.kit.utxo

import fr.acinq.bitcoin.*
import fr.acinq.secp256k1.Hex


fun String.FlipByteOrder(): String {
    return this.chunked(2).reversed().joinToString("")
}



fun main() {

    val privateKey = PrivateKey.fromHex("58badd9b455145b487ad24c7f259cc437cc4ffd0784716845249321fb5961cfc")

    //  Mainnet
    val wifMainnet = privateKey.toBase58(0x80.toByte())

    // Testnet
    val wifTestnet = privateKey.toBase58(0xEF.toByte())

    //println(wifMainnet) // KzCBzQzuMFSfPX98wKAstJZrsM6dSW88YofEmBtJTSz85qvJEnEd
    //println(wifTestnet) // cQZBTKzknK8vYxcQKiz1Fd4vVaQ36xDpcqohscLoxZe8Lb18d6U4


    val wifPrivateKey = PrivateKey.fromBase58(
        wifTestnet,
        Base58.Prefix.SecretKeyTestnet
    ).first

    //println(wifPrivateKey.value)
    println("private key : ${wifPrivateKey.value}")

    val publicKey = privateKey.publicKey()
    println("public key : $publicKey")


    val redeemScriptHex = "03c26b27b1752102821432bbb61bbfc1eecd99ddf28caf65e3d7ba5dd6d12a8b853914fc4f5b3ba7ac"
    val redeemScripBytes = Hex.decode(redeemScriptHex)

    val scriptElements: List<ScriptElt> = Script.parse(redeemScripBytes)
    println("contract : $scriptElements")


    val script = Script.pay2sh(scriptElements)
    println("locking script : $script")

    val addrMain = Bitcoin.addressFromPublicKeyScript(Block.LivenetGenesisBlock.hash, script).right
    //println("mainnet : $addrMain")


    // สร้าง P2SH address โดยใช้ script และ LivenetGenesisBlock.hash
    val addrTest = Bitcoin.addressFromPublicKeyScript(Block.TestnetGenesisBlock.hash, script).right

    println("testnet : $addrTest")


    // ----------------------------------------------------------------


    val addressSigwit = "tb1qe6s6js6883jd8vu8seurkwq840e3urzgz6jpvx"
    val (hrp, version, data) = Bech32.decodeWitnessAddress(addressSigwit)


//    val previousTx = Transaction.read(
//        "0200000000010153265a088e0bd32f7314d7a0eda3085a1156dff100858b4b454cf2139402bf970000000000fdffffff02fc0800000000000017a914bae8a0e015a59f92975f3f4d4b2695f326415b9c876c4b1d0000000000160014d4907ed55d4e87f3d9f421e5495d0ce9d65d72b40247304402202957541a824c36fa5de7e7d8696a89a6571c378ee8ed0c3c3fc983a3973a208d02206f09ab0479b136301defacbe9155431b422d88ede45d8d69b961f51bb0e614940121021576507dc50f08dcd1c4bb21474ad5e97af6e52a190a54d8e414ab1ad90beca600000000"
//    )

    val txInID = "8f835cdb62cda09437c7194c75b3f09f3aeede4757ebd5282f54753d5685149d".FlipByteOrder()

    val send = listOf(OP_0, OP_PUSHDATA(data))

    println("\nlocking script : $send")
    println("send to (p2wpkh) : $addressSigwit")

    val draftUTxO = Transaction(
        version = 1L,
        txIn = listOf(
            TxIn(
                OutPoint(
                    TxHash(txInID),
                    0
                ),
                signatureScript = redeemScripBytes,
                sequence = 0xFFFFFFFFD
            )
        ),
        txOut = listOf(
            TxOut(
                amount = 2300.toSatoshi(),
                publicKeyScript = send
            )
        ),
        lockTime = 2583490
    )

    val sigUTxO = Transaction.signInput(draftUTxO, 0, redeemScripBytes, SigHash.SIGHASH_ALL, privateKey)
    val finalUTxO = draftUTxO.updateSigScript(0, listOf(OP_PUSHDATA(sigUTxO), OP_PUSHDATA(redeemScripBytes)))

    println("\nDraft UTxO :\n$draftUTxO")
    println("Signed UTxO :\n${finalUTxO}")
}