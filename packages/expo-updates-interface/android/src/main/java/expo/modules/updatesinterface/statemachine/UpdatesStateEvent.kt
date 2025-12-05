package expo.modules.updatesinterface.statemachine


import org.json.JSONObject
import java.util.Date

/**
Structure representing an event that can be sent to the machine.
 */
sealed class UpdatesStateEvent(val type: expo.modules.updatesinterface.statemachine.UpdatesStateEventType) {
  class StartStartup : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.StartStartup)
  class EndStartup : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.EndStartup)
  class Check : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.Check)
  class CheckCompleteWithUpdate(val manifest: JSONObject) : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.CheckCompleteAvailable)
  class CheckCompleteWithRollback(val commitTime: Date) : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.CheckCompleteAvailable)
  class CheckCompleteUnavailable : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.CheckCompleteUnavailable)
  class CheckError(private val errorMessage: String) : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.CheckError) {
    val error: UpdatesStateError
      get() {
        return UpdatesStateError(errorMessage)
      }
  }
  class Download : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.Download)
  class DownloadProgress(val progress: Double) : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.DownloadProgress)
  class DownloadComplete : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.DownloadComplete)
  class DownloadCompleteWithUpdate(val manifest: JSONObject) : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.DownloadComplete)
  class DownloadCompleteWithRollback : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.DownloadComplete)
  class DownloadError(private val errorMessage: String) : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.DownloadError) {
    val error: UpdatesStateError
      get() {
        return UpdatesStateError(errorMessage)
      }
  }
  class Restart : UpdatesStateEvent(expo.modules.updatesinterface.statemachine.UpdatesStateEventType.Restart)
}
