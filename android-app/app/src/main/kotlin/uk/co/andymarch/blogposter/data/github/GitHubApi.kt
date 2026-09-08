package uk.co.andymarch.blogposter.data.github

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Thin wrapper over the subset of the GitHub REST API this app needs: who am I,
 * can I push to this repo, and read/write files through the Contents API (which
 * commits directly to a branch — no local git checkout required on-device).
 */
interface GitHubApi {

    @GET("user")
    suspend fun getAuthenticatedUser(): GitHubUserDto

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): GitHubRepoDto

    /** Returns null (404) when the file doesn't exist yet at [ref]. */
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path(value = "path", encoded = true) path: String,
        @Query("ref") ref: String,
    ): Response<GitHubContentDto>

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun listDirectory(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path(value = "path", encoded = true) path: String,
        @Query("ref") ref: String,
    ): Response<List<GitHubContentDto>>

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun putFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path(value = "path", encoded = true) path: String,
        @Body request: PutContentRequest,
    ): PutContentResponse

    @HTTP(method = "DELETE", path = "repos/{owner}/{repo}/contents/{path}", hasBody = true)
    suspend fun deleteFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path(value = "path", encoded = true) path: String,
        @Body request: DeleteContentRequest,
    ): PutContentResponse
}
