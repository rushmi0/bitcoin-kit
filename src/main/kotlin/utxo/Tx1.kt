package org.bitcoin.kit.utxo

import fr.acinq.bitcoin.*
import fr.acinq.bitcoin.SigHash.SIGHASH_ALL
import fr.acinq.secp256k1.Hex


fun main() {

    val address = "mi1cMMSL9BZwTQZYpweE1nTmwRxScirPp3"
    val (prefix, pubkeyHash) = Base58Check.decode(address)

    println(
        "$prefix : $pubkeyHash"
    )

    val amount = 1000L.toSatoshi()

    val privateKey = PrivateKey.fromBase58(
        "cRp4uUnreGMZN8vB7nQFX6XWMHU5Lc73HMAhmcDEwHfbgRS66Cqp",
        Base58.Prefix.SecretKeyTestnet
    ).first
    val publicKey = privateKey.publicKey()

    val previousTx = Transaction.read(
        "0100000001b021a77dcaad3a2da6f1611d2403e1298a902af8567c25d6e65073f6b52ef12d000000006a473044022056156e9f0ad7506621bc1eb963f5133d06d7259e27b13fcb2803f39c7787a81c022056325330585e4be39bcf63af8090a2deff265bc29a3fb9b4bf7a31426d9798150121022dfb538041f111bb16402aa83bd6a3771fa8aa0e5e9b0b549674857fafaf4fe0ffffffff0210270000000000001976a91415c23e7f4f919e9ff554ec585cb2a67df952397488ac3c9d1000000000001976a9148982824e057ccc8d4591982df71aa9220236a63888ac00000000"
    )

    println("> $previousTx")

    val redeemScriptHex = "03beb30bb17521027de11310f7c996a2d1021276c11759ebb6f26d229dfd0bbc93b7f72fd36e3b8cac"
    val bytes = Hex.decode(redeemScriptHex)

    val scriptElements: List<ScriptElt> = Script.parse(bytes)

    // create a transaction where the sig script is the pubkey script of the tx we want to redeem
    // the pubkey script is just a wrapper around the pub key hash
    // what it means is that we will sign a block of data that contains txid + from + to + amount

    // step  #1: creation a new transaction that reuses the previous transaction's output pubkey script
    val tx1 = Transaction(
        version = 1L,
        txIn = listOf(
            TxIn(
                OutPoint(previousTx.hash, 0),
                signatureScript = scriptElements,
                sequence = 0xFFFFFFFFL
            )
        ),
        txOut = listOf(
            TxOut(
                amount = amount,
                publicKeyScript = listOf(OP_DUP, OP_HASH160, OP_PUSHDATA(pubkeyHash), OP_EQUALVERIFY, OP_CHECKSIG)
            )
        ),
        lockTime = 766910
    )

    val data = previousTx.txOut[0].publicKeyScript
    println("#:$data")

    // step #2: sign the tx
    val sig = Transaction.signInput(tx1, 0, bytes, SIGHASH_ALL, privateKey)
    val tx2 = tx1.updateSigScript(0, listOf(OP_PUSHDATA(sig), OP_PUSHDATA(bytes)))

    println("tx1: $tx1")
    println("tx2: $tx2")

    // redeem the tx
    Transaction.correctlySpends(tx2, listOf(previousTx), ScriptFlags.MANDATORY_SCRIPT_VERIFY_FLAGS)

}
