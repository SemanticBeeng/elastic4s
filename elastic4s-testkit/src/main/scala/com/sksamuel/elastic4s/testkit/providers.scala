package com.sksamuel.elastic4s.testkit

import java.nio.file.{Path, Paths}
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

import com.sksamuel.elastic4s.embedded.LocalNode

// LocalNodeProvider provides helper methods to create a local (embedded) node
trait LocalNodeProvider {
  // returns an embedded, started, node
  def getNode: LocalNode
}

// implementation of LocalNodeProvider that uses a single
// node instance for all classes in the same classloader.
trait ClassloaderLocalNodeProvider extends LocalNodeProvider {
  override def getNode: LocalNode = ClassloaderLocalNodeProvider.node
}

object ClassloaderLocalNodeProvider {
  private lazy val tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")
  private lazy val pathHome: Path = tempDirectoryPath resolve UUID.randomUUID().toString
  lazy val node = LocalNode("classloader-node", pathHome.toAbsolutePath.toString)
}

// implementation of LocalNodeProvider that uses a single
// node instance for each class that mixes in this trait.
trait ClassLocalNodeProvider extends LocalNodeProvider {

  private lazy val tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")
  private lazy val pathHome: Path = tempDirectoryPath resolve UUID.randomUUID().toString

  override lazy val getNode = LocalNode(
    "node_" + ClassLocalNodeProvider.counter.getAndIncrement(),
    pathHome.toAbsolutePath.toString
  )
}

object ClassLocalNodeProvider {
  val counter = new AtomicLong(1)
}
