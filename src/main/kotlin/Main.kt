import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import tests.runner.TestRunner
import kotlin.system.exitProcess


class CocosSil : CliktCommand() {
    private val cocosBinary by option(help="Cocos Binary").path().required()

    override fun run() {
        exitProcess(if (TestRunner(cocosBinary).runTests()) { 0 } else { 1 })
    }
}
fun main(args: Array<String>) {
    CocosSil().main(args)
}