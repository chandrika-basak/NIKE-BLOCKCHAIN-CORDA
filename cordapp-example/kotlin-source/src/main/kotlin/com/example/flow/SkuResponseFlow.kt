package com.example.flow

import co.paralleluniverse.fibers.Suspendable
import com.example.contract.SkuTransferContract
import com.example.model.SkuRequestModel


import com.example.state.SkuRequestState



import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.core.utilities.unwrap

import java.time.LocalDateTime
import java.io.IOException
import java.net.MalformedURLException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.sql.SQLException
import java.sql.ResultSet
import java.sql.PreparedStatement
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder
import scala.sys.process.ProcessBuilderImpl
import java.util.*


/**
 * Created by cordadev1 on 4/4/2018.
 */

/**
 * This flow allows network participant to broadcast request for an SKU.
 * Request details are present in [SkuRequestState]
 *
 * In our flow, the [Acceptor] always accepts a valid request.
 *
 * All methods called within the [FlowLogic] sub-class need to be annotated with the @Suspendable annotation.
 */
object SkuResponseFlow {
    @InitiatingFlow
    @StartableByRPC
    class Initiator(val skuRequestModel:SkuRequestModel, val recipient :Party) : FlowLogic<SkuRequestState>() {
        /**
         * The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
         * checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call() function.
         */
        companion object {
            object GENERATING_TRANSACTION : Step("Generating transaction based on SKU request")
            object VERIFYING_TRANSACTION : Step("Verifying contract constraints.")
            object SIGNING_TRANSACTION : Step("Signing transaction with our private key.")
            object GATHERING_SIGS : Step("Gathering the counterparty's signature.") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING_TRANSACTION : Step("Obtaining notary signature and recording transaction.") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                    GENERATING_TRANSACTION,
                    VERIFYING_TRANSACTION,
                    SIGNING_TRANSACTION,
                    GATHERING_SIGS,
                    FINALISING_TRANSACTION
            )
        }

        override val progressTracker = tracker()

        /**
         * The flow logic is encapsulated within the call() method.
         */
        @Suspendable
        override fun call(): SkuRequestState {

            System.out.println("***** Inside SKU MarketPlaceSearch Initiator I am :"+serviceHub.myInfo.legalIdentities.first())
            logger.info("***** Inside SKU MarketPlaceSearch Initiator I am :"+serviceHub.myInfo.legalIdentities.first())
            System.out.println("***** Inside SKU MarketPlaceSearch Recipient Is :"+recipient.toString())
            logger.info("***** Inside SKU MarketPlaceSearch Initiator Recipient Is :"+recipient.toString())
            val counterpartySession = initiateFlow(recipient)
            //bcounterpartySession.sendAndReceive<SkuResponseState>(skuRequestState)

            val packet2 = counterpartySession.sendAndReceive(SkuRequestState::class.java, skuRequestModel)
            val unwrappedSkuRepState = packet2.unwrap({ data ->
                println(data)
                println("Inside Initiaor")
                return data
            })




            return unwrappedSkuRepState
        }
    }
    @InitiatedBy(SkuResponseFlow.Initiator::class)
    class Acceptor(val otherPartyFlow: FlowSession) : FlowLogic<Unit>() {
        @Suspendable
        override fun call(): Unit {

            System.out.println("***** Inside SKU MarketPlaceSearch Acceptor I am :"+serviceHub.myInfo.legalIdentities.first())
            logger.info("***** Inside SKU MarketPlaceSearch Acceptor I am :"+serviceHub.myInfo.legalIdentities.first())
            val obj = otherPartyFlow.receive(SkuRequestModel::class.java).unwrap({ data -> data });
            val skuRequestState = prepareResultState(obj)

            otherPartyFlow.send(skuRequestState)
            //val fullySignedTx = subFlow(ResponseLedgerWriteFlow.Initiator(skuRessponseState))


        }

        private fun prepareResultState(obj: SkuRequestModel): SkuRequestState {
            var conn: HttpURLConnection? = null
            var availability : String? = null
            var status : String? = null
            try {
                //URL url = new URL("http://52.0.75.110:9984/api/productInventory/" + queryStr);
                val connection = serviceHub.jdbcSession()
                var statement: PreparedStatement? = null
                var rs: ResultSet?=null
                var sapUrl =""

                try {
                    var queryString = "SELECT URL FROM SAP_CONNECT"
                    statement = connection.prepareStatement(queryString)

                    rs = statement.executeQuery()
                    if (rs != null) {
                        while (rs.next()) {
                            sapUrl=  rs.getString("URL")
                        }
                    }
                }
                catch (se:SQLException){
                    print(se)
                }


                val url1 = sapUrl+obj.skuID
                val extURL = URL(url1)

                with(extURL.openConnection() as HttpURLConnection) {
                    // optional default is GET
                    requestMethod = "GET"


                    println("\nSending 'GET' request to URL : $url1")
                    println("Response Code : $responseCode")

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        println(response.toString())
                        availability = response.toString()
                        status = "Active"
                    }
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }



            val random = Random()
            var finalSkuRequesteModel = SkuRequestModel(responseID = "RESPONSE"+System.currentTimeMillis()+random.nextInt(99),requestID = obj.requestID,skuID = obj.skuID,itemAvailability = availability!!,requestTime = LocalDateTime.now());

            var finalSkuRequesteState = SkuRequestState(skuRequestModel= finalSkuRequesteModel,sender = serviceHub.myInfo.legalIdentities.first(),recipient = otherPartyFlow.counterparty)


            return finalSkuRequesteState
        }
    }

}

