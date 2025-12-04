// Copyright 2019 650 Industries. All rights reserved.

import ExpoModulesCore
import EXUpdatesInterface

let e2eEventName = "updatesStateDidChange"

public final class E2ETestModule: Module, UpdatesStateChangeListener {
  private let methodQueue = DispatchQueue(label: "expo.modules.EXUpdatesQueue")
  private var updatesController: (any UpdatesEnabledInterface)?
  private var hasListener: Bool = false

  public func updatesStateDidChange(_ event: UpdatesStateEvent) {
    if (hasListener) {
      var payload: [String: Any] = [ "type" : "\(event.type)" ]
      switch (event) {
      case let .checkCompleteWithUpdate(manifest):
        payload["manifest"] = manifest
        break;
      case let .downloadCompleteWithUpdate(manifest):
        payload["manifest"] = manifest
        break;
      default:
        break;
      }
      sendEvent(e2eEventName, payload)
    }
  }

  public required init(appContext: AppContext) {
    super.init(appContext: appContext)
  }

  public func definition() -> ModuleDefinition {
    Name("ExpoUpdatesE2ETest")

    Events([e2eEventName])

    OnCreate {
      if let controller = UpdatesControllerRegistry.sharedInstance.controller as? UpdatesEnabledInterface {
        updatesController = controller
        controller.stateChangeListener = self
      }
    }

    OnStartObserving {
      hasListener = true
    }

    OnStopObserving {
      hasListener = false
    }

    OnDestroy {
      updatesController = nil
      if let controller = UpdatesControllerRegistry.sharedInstance.controller as? UpdatesEnabledInterface {
        controller.stateChangeListener = nil
      }
    }

    Function("launchedUpdateId") {
      return updatesController?.launchedUpdateId
    }

    Function("embeddedUpdateId") {
      return updatesController?.embeddedUpdateId
    }

    Function("runtimeVersion") {
      return updatesController?.runtimeVersion
    }

    AsyncFunction("readInternalAssetsFolderAsync") { (promise: Promise) in
      updatesController?.getInternalDbAssetCountAsync(promise)
    }

    AsyncFunction("clearInternalAssetsFolderAsync") { (promise: Promise) in
      updatesController?.clearInternalDbAssetsAsync(promise)
    }
  }
}

extension UpdatesEnabledInterface {
  func getInternalDbAssetCountAsync(_ promise: Promise) {}
  func clearInternalDbAssetsAsync(_ promise: Promise) {}
}


