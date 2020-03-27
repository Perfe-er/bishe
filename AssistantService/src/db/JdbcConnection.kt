package db

import online.sanen.cdm.api.Bootstrap
import online.sanen.cdm.api.basic.DriverOption
import online.sanen.cdm.core.factory.Bootstraps



object JdbcConnection {

    lateinit var bootstrap : Bootstrap

    fun initConnection(){

        System.out.println("开始启动数据库连接")
         bootstrap = Bootstraps.load("default") { configuration ->
            configuration.setDriverOption(DriverOption.MYSQL_CJ)
            configuration.setUrl(DB_URL)
            configuration.setUsername(DB_USER)
            configuration.setPassword(DB_PASSWORD)
        }

//打印当前连接所有表名（测试输出）
        System.out.println(bootstrap.dataInformation().tableNames)
    }

}