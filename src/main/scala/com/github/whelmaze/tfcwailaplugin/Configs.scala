package com.github.whelmaze.tfcwailaplugin

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaRegistrar}

case class Config(keyName: String, description: String, default: Boolean) {
  def isEnabled(implicit config: IWailaConfigHandler): Boolean = config.getConfig(s"${Configs.keyNamePrefix}.$keyName")
  def isDisabled(implicit config: IWailaConfigHandler): Boolean = !isEnabled
}

object Configs {

  val modName = "TFCWailaPlugin"
  val keyNamePrefix = "tfcwailaplugin"

  // define configs
  val numberOfLog = Config("numberoflog", "Show number of log", default = false)
  val logPerSlot = Config("logperslot", "Show log per slot", default = false)
  val noLogInfo = Config("nologinfo", "No log pile Info", default = false)

  // register configs
  def registerAll(registrar: IWailaRegistrar): Unit = {
    def register(cfgs: Config*) = cfgs.distinct.foreach { cfg =>
      registrar.addConfig(modName, s"$keyNamePrefix.${cfg.keyName}", cfg.description, cfg.default)
    }

    register (
      // TELogPile
      numberOfLog, logPerSlot, noLogInfo
    )
  }

}
