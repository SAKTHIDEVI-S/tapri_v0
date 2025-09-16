package com.tapri.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface EarnApi {
    @GET("jobs")
    suspend fun listJobs(): Response<List<JobItem>>

    @GET("jobs/{id}")
    suspend fun getJobDetails(@Path("id") id: Long): Response<JobItem>

    @POST("jobs/{id}/claim")
    suspend fun claimJob(@Path("id") id: Long): Response<ClaimResponse>

    @GET("claims/ongoing")
    suspend fun getOngoingClaim(): Response<JobClaim>

    @POST("claims/{claimId}/submit")
    suspend fun submitProof(
        @Path("claimId") claimId: Long,
        @Body body: SubmitProofRequest
    ): Response<JobClaim>
}

data class JobItem(
    val id: Long,
    val title: String,
    val description: String?,
    val payout: Double?,
    val pay: Double?, // Added pay field
    val hourlyRate: Double?,
    val durationMinutes: Int?,
    val startsAt: String?,
    val endsAt: String?,
    val isActive: Boolean?,
    val location: String?,
    val contactPhone: String?,
    val requirements: String?,
    val instructions: String?,
    val pickupProofRequired: Boolean?,
    val dropoffProofRequired: Boolean?
)

data class ClaimResponse(
    val claimId: Long?,
    val dueAt: String?
)

data class JobClaim(
    val id: Long,
    val status: String?,
    val dueAt: String?,
    val job: JobItem?
)

data class SubmitProofRequest(
    val proofUrl: String?,
    val notes: String?
) 