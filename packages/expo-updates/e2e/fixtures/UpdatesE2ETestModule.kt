package expo.modules.updates

import android.os.Bundle
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.types.Enumerable
import expo.modules.updatesinterface.UpdatesControllerRegistry
import expo.modules.updatesinterface.UpdatesEnabledInterface
import expo.modules.updatesinterface.UpdatesStateChangeListener
import expo.modules.updatesinterface.statemachine.UpdatesStateEvent

interface UpdatesEnabledTestingInterface: UpdatesEnabledInterface {
  fun clearInternalAssetsFolderAsync(promise: Promise)
  fun readInternalAssetsFolderAsync(promise: Promise)
}

class UpdatesE2ETestModule : Module(), UpdatesStateChangeListener {
  private var hasListener: Boolean = false
  private var updatesController: UpdatesEnabledInterface? = null

  override fun definition() = ModuleDefinition {
    Name("ExpoUpdatesE2ETest")

    Events<UpdatesE2EEvent>()

    OnCreate {
      UpdatesControllerRegistry.controller?.get()?.let {
        if (it is UpdatesEnabledInterface) {
          updatesController = it
          it.stateChangeListener = this@UpdatesE2ETestModule
        }
      }
    }

    OnStartObserving(UpdatesE2EEvent.StateChange) {
      hasListener = true
    }

    OnStopObserving(UpdatesE2EEvent.StateChange) {
      hasListener = false
    }

    OnDestroy {
      updatesController?.stateChangeListener = null
      updatesController = null
    }

    Function("getLaunchedUpdateId") {
      return@Function updatesController?.launchedUpdateId?.toString()
    }

    Function("getEmbeddedUpdateId") {
      return@Function updatesController?.embeddedUpdateId?.toString()
    }

    Function("getRuntimeVersion") {
      return@Function updatesController?.runtimeVersion
    }


    AsyncFunction("clearInternalAssetsFolderAsync") { promise: Promise ->
      updatesController?.let {
        if (it is UpdatesEnabledTestingInterface) {
          it.clearInternalAssetsFolderAsync(promise)
        }
      }
    }

    AsyncFunction("readInternalAssetsFolderAsync") { promise: Promise ->
      updatesController?.let {
        if (it is UpdatesEnabledTestingInterface) {
          it.readInternalAssetsFolderAsync(promise)
        }
      }
    }
  }

  override fun updatesStateDidChange(event: UpdatesStateEvent) {
    if (hasListener) {
      val payload = Bundle()
      payload.putString("type", event.type.toString())
      when(event) {
        is UpdatesStateEvent.CheckCompleteWithUpdate -> {
          val manifest = event.manifest
          val manifestBundle = Bundle()
          manifestBundle.putString("id", manifest.getString("id"))
          payload.putBundle("manifest", manifestBundle)
          payload.putString("type", "checkCompleteWithUpdate")
        }
        is UpdatesStateEvent.CheckCompleteWithRollback -> {
          payload.putString("type", "checkCompleteWithRollback")
        }
        is UpdatesStateEvent.DownloadCompleteWithUpdate -> {
          val manifest = event.manifest
          val manifestBundle = Bundle()
          manifestBundle.putString("id", manifest.getString("id"))
          payload.putBundle("manifest", manifestBundle)
        }
        else -> {}
      }
      sendEvent(UpdatesE2EEvent.StateChange, payload)
    }
  }
}

enum class UpdatesE2EEvent(val eventName: String) : Enumerable {
  StateChange("Expo.updatesE2EStateChangeEvent")
}
