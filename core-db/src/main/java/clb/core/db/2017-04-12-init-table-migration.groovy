package crlandMDM.core.db.table


import com.hand.hap.liquibase.MigrationHelper
def mhi = MigrationHelper.getInstance()
dbType = MigrationHelper.getInstance().dbType()

databaseChangeLog(logicalFilePath:"clb/core/db/2017-04-12-init-migration.groovy"){
    changeSet(author: "wanjun.feng, id: "2017041201") {
        sqlFile(path: mhi.dataPath("clb/core/db/patch/"+dbType+"/FMS_PRD_CLASS_SET.sql"), encoding: "UTF-8")
    }
    changeSet(author: "wanjun.feng, id: "2017041202") {
        sqlFile(path: mhi.dataPath("clb/core/db/patch/"+dbType+"/FMS_PRD_CLASS.sql"), encoding: "UTF-8")
    }
    changeSet(author: "wanjun.feng, id: "2017041801") {
        sqlFile(path: mhi.dataPath("clb/core/db/patch/"+dbType+"/FMS_PRD_DISCOUNT.sql"), encoding: "UTF-8")
    }
    changeSet(author: "wanjun.feng, id: "2017041901") {
        sqlFile(path: mhi.dataPath("clb/core/db/patch/"+dbType+"/FMS_CNL_PAYMENT_TERM.sql"), encoding: "UTF-8")
    }
}