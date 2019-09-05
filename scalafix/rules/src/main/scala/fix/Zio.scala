package fix

import scalafix.v1._

import scala.meta._

class Zio extends SemanticRule("Zio") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    Patch.replaceSymbols(
      "cats/effect/IO" -> "zio/Task",
      "cats/effect/IOApp" -> "zio/App"
    ) ++ doc.tree.collect {
      case pureMatcher(Term.Apply(Term.Select(_, s), _)) =>
        Patch.replaceTree(s, "succeed")
      case raiseErrorMatcher(Term.Apply(Term.Select(_, s), _)) =>
        Patch.replaceTree(s, "fail")
      case exitCodeSuccessMatcher(s) =>
        if (s.toString().endsWith("Success"))
          Patch.replaceTree(s, "0")
        else if (s.toString().endsWith("Error"))
          Patch.replaceTree(s, "1")
        else Patch.empty
//      case exitCodeErrorMatcher(s) =>
//        Patch.replaceTree(s, "1")
      case t @ unsafeRunSyncMatcher(Term.Apply(Term.Select(term, s), _)) =>
        t.parent.map { parentTree =>
          Patch.addGlobalImport(Symbol("zio/DefaultRuntime.")) +
            Patch.addLeft(parentTree, "val runtime: DefaultRuntime = new DefaultRuntime {}\n") +
            s.parent.map { sParent =>
              Patch.replaceTree(sParent, "runtime.unsafeRun(" + term.toString() + ")")
              //Patch.addLeft(sParent, "runtime.unsafeRun")
            }.getOrElse(Patch.empty)
//            Patch.replaceTree(s, "runtime.unsafeRun(" + term.toString() + ")")
        }.getOrElse(Patch.empty)
      //val runtime: DefaultRuntime = new DefaultRuntime {}
    } + Patch.removeImportee(Importee.Name(Name("cats/effect/IO")))
  }

  val pureMatcher = SymbolMatcher.normalized("cats/effect/IO#pure.")
  val raiseErrorMatcher = SymbolMatcher.normalized("cats/effect/IO#raiseError.")
  val unsafeRunSyncMatcher = SymbolMatcher.normalized("cats/effect/IO#unsafeRunSync")
  val exitCodeSuccessMatcher = SymbolMatcher.exact("cats/effect/ExitCode.Success.", "cats/effect/ExitCode.Error.")
  //val exitCodeErrorMatcher = SymbolMatcher.exact("cats/effect/ExitCode.Error.")
}
