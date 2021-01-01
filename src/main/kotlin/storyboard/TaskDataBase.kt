package storyboard

import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.time.LocalDateTime

class TaskDataBase(
    val name: String = "myDB",
    val url: String = "jdbc:sqlite:${Paths.get("")}$name.db"
) {
    lateinit var conn: Connection
    lateinit var stmt: Statement

    init {
        conn = DriverManager.getConnection(url)
        stmt = conn.createStatement()
        stmt.execute(createNewTable())
        conn.apply {
            println("driver name: ${conn.metaData.driverName}")
        }
    }

    fun selectAll() = "SELECT * FROM ${TableInfo.TABLE_NAME}"

    fun numberOfTasks() =
        stmt.executeQuery("SELECT COUNT(*) FROM ${TaskDataBase.TableInfo.TABLE_NAME}").getString(1).toInt()

    fun getTask(id: Int): Task {
        val qry = "SELECT * FROM ${TableInfo.TABLE_NAME} WHERE ${TableInfo.COLUMN_ITEM_ID}=$id"
        val rs = stmt.executeQuery(qry)
        var task = Task(
            rs.getString(TaskDataBase.TableInfo.COLUMN_ITEM_NAME),
            rs.getString(TaskDataBase.TableInfo.COLUMN_ITEM_DESCRIPTION)
        )
        task.id = rs.getInt(TaskDataBase.TableInfo.COLUMN_ITEM_ID).toLong()
        task.state = ItemState.valueOf(rs.getString(TaskDataBase.TableInfo.COLUMN_ITEM_STATE))
        task.priority = rs.getInt(TaskDataBase.TableInfo.COLUMN_ITEM_PRIORITY)
        task.stackRank = rs.getInt(TaskDataBase.TableInfo.COLUMN_ITEM_STACK_RANK).toLong()
        task.effortEstimationInHours = rs.getInt(TaskDataBase.TableInfo.COLUMN_ITEM_EFFORT_HOURS)
        task.creationDate = dateOrNull(rs.getString(TaskDataBase.TableInfo.COLUMN_ITEM_CREATION_DATE))
        task.creationDate = dateOrNull(rs.getString(TaskDataBase.TableInfo.COLUMN_ITEM_CREATION_DATE))
        task.deliveryDate = dateOrNull(rs.getString(TaskDataBase.TableInfo.COLUMN_ITEM_DELIVERY_DATE))
        task.lastModificationDate = dateOrNull(rs.getString(TaskDataBase.TableInfo.COLUMN_ITEM_LAST_MODIFICATION_DATE))
        task.dependencies = stringToSet(rs.getString(TaskDataBase.TableInfo.COLUMN_ITEM_DEPENDENCIES))
        return task
    }

    private fun stringToSet(string: String): MutableSet<Long> {
        val set = mutableSetOf<Long>()
        if (string != "[]")
            string.split(",").forEach {
                set.add(it.toLong())
            }
        return set
    }

    private fun dateOrNull(string: String): LocalDateTime? {
        return if (string == "null")
            null
        else
            LocalDateTime.parse(string, StoryBoardUtil.dateFormatter)
    }

    private fun createNewTable(): String = "CREATE TABLE IF NOT EXISTS ${TableInfo.TABLE_NAME} (\n" +
            "${TableInfo.COLUMN_ITEM_ID} integer PRIMARY KEY,\n" +
            "${TableInfo.COLUMN_ITEM_NAME} text NOT NULL,\n" +
            "${TableInfo.COLUMN_ITEM_DESCRIPTION} text,\n" +
            "${TableInfo.COLUMN_ITEM_STATE} text,\n" +
            "${TableInfo.COLUMN_ITEM_PRIORITY} integer,\n" +
            "${TableInfo.COLUMN_ITEM_STACK_RANK} integer,\n" +
            "${TableInfo.COLUMN_ITEM_EFFORT_HOURS} integer,\n" +
            "${TableInfo.COLUMN_ITEM_CREATION_DATE} text NOT NULL,\n" +
            "${TableInfo.COLUMN_ITEM_DELIVERY_DATE} text,\n" +
            "${TableInfo.COLUMN_ITEM_LAST_MODIFICATION_DATE} text NOT NULL,\n" +
            "${TableInfo.COLUMN_ITEM_DEPENDENCIES} text\n" +
            ");"

    object TableInfo {
        const val TABLE_NAME = "StoryBoard"
        const val COLUMN_ITEM_ID = "itemID"
        const val COLUMN_ITEM_NAME = "itemName"
        const val COLUMN_ITEM_DESCRIPTION = "itemDescription"
        const val COLUMN_ITEM_STATE = "itemState"
        const val COLUMN_ITEM_PRIORITY = "itemPriority"
        const val COLUMN_ITEM_STACK_RANK = "itemStackRank"
        const val COLUMN_ITEM_EFFORT_HOURS = "itemEffort"
        const val COLUMN_ITEM_CREATION_DATE = "itemCreationDate"
        const val COLUMN_ITEM_DELIVERY_DATE = "itemDeliveryDate"
        const val COLUMN_ITEM_LAST_MODIFICATION_DATE = "itemLastModificationDate"
        const val COLUMN_ITEM_DEPENDENCIES = "itemDependencies"
    }

    fun saveTask(task: Task) {
        val req =
            "INSERT OR REPLACE INTO ${TaskDataBase.TableInfo.TABLE_NAME}" +
                    "(" +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_ID}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_NAME}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_DESCRIPTION}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_STATE}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_PRIORITY}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_STACK_RANK}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_EFFORT_HOURS}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_CREATION_DATE}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_DELIVERY_DATE}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_LAST_MODIFICATION_DATE}," +
                    "${TaskDataBase.TableInfo.COLUMN_ITEM_DEPENDENCIES}" +
                    ") " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?)"
        val updateTime = LocalDateTime.now()
        val prepStatement = conn.prepareStatement(req)
        prepStatement.setString(1, task.id.toString())
        task.id?.let {
            prepStatement.setLong(1, it)
        } ?: prepStatement.setNull(1, java.sql.Types.INTEGER)
        prepStatement.setString(2, task.name)
        prepStatement.setString(3, task.description)
        prepStatement.setString(4, task.state.toString())
        prepStatement.setInt(5, task.priority)
        task.stackRank.let {
            prepStatement.setLong(6, it)
        }
        task.effortEstimationInHours.let {
            prepStatement.setInt(7, it)
        }
        task.creationDate?.let {
            prepStatement.setString(8, it.format(StoryBoardUtil.dateFormatter))
        } ?: prepStatement.setString(8, updateTime.format(StoryBoardUtil.dateFormatter))
        prepStatement.setString(9, task.deliveryDate.toString())
        prepStatement.setString(10, updateTime.format(StoryBoardUtil.dateFormatter))
        prepStatement.setString(11, task.dependencies.toString())

        val rowCount = prepStatement.executeUpdate()
        if (rowCount > 0) {
            if (task.id == null)
                task.id = stmt.generatedKeys.getLong(1)
            task.lastModificationDate = updateTime
            if (task.creationDate == null)
                task.creationDate = updateTime
        }
    }
}