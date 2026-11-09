package id.ikuradachi.patches.crunchyroll.performance

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import id.ikuradachi.patches.crunchyroll.shared.Constants.COMPATIBILITY_CRUNCHYROLL

private const val RETURN_SUCCESS = """
    new-instance v0, Landroidx/work/n${'$'}a${'$'}c;
    invoke-direct {v0}, Landroidx/work/n${'$'}a${'$'}c;-><init>()V
    return-object v0
"""

@Suppress("unused")
val suppressBloatWorkersPatch = bytecodePatch(
    name = "Suppress bloat workers",
    description = "Forces Google Engage, Continue Watching, Datadog, and OneTrust CMP background workers to return success immediately without performing network requests, disk I/O, or image prefetching.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_CRUNCHYROLL)

    execute {
        // Target 7: Google Engage service worker execution
        GoogleEngageWorkerDoWorkFingerprint.matchAll(1..1).single().method.addInstructions(
            0,
            RETURN_SUCCESS.trimIndent(),
        )

        // Target 8: Continue Watching recurring background worker execution
        ContinueWatchingWorkerDoWorkFingerprint.matchAll(1..1).single().method.addInstructions(
            0,
            RETURN_SUCCESS.trimIndent(),
        )

        // Target 9: Datadog WorkManager upload worker scheduler
        DatadogWorkManagerSchedulerFingerprint.matchAll(1..1).single().method.addInstruction(0, "return-void")

        // Target 10: Datadog WorkManager upload worker execution
        DatadogUploadWorkerDoWorkFingerprint.matchAll(1..1).single().method.addInstructions(
            0,
            RETURN_SUCCESS.trimIndent(),
        )

        // Target 11: OneTrust CMP consent logging worker execution
        OneTrustConsentLoggingWorkerFingerprint.matchAll(1..1).single().method.addInstructions(
            0,
            RETURN_SUCCESS.trimIndent(),
        )
    }
}
