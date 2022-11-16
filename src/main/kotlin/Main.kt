import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import tests.runner.TestRunner
import kotlin.system.exitProcess


class CocosSil : CliktCommand() {
    val gpioStream by option(help="GPIO State Stream").file().required()

    override fun run() {
        exitProcess(if (TestRunner().runTests(gpioStream)) { 0 } else { 1 })
    }
}
fun main(args: Array<String>) {
    CocosSil().main(args)
}