import org.apache.commons.compress.archivers.zip.*
import org.apache.commons.compress.utils.*

def writeEntries = { aos, base, path ->
  if( path.isDirectory() ){
    def children = path.listFiles()
    if( children.length == 0 ){
      // ディレクトリ追加
      def fn = path.getAbsolutePath().substring(
        base.getAbsolutePath().length()+1).
        replace(File.separator,'/')
      println("creating...:" + fn)
      ent = new ZipArchiveEntry(path, fn)
      aos.putArchiveEntry(ent)
      aos.closeArchiveEntry()
    } else {
      for(child in path.listFiles()){
        call(aos, base, child)
      }
    }
  } else {
    // ファイル追加
    def fn = path.getAbsolutePath().substring(
      base.getAbsolutePath().length()+1).
      replace(File.separator,'/')
    println("adding...:" + fn)
    ent = aos.createArchiveEntry(path, fn)
    aos.putArchiveEntry(ent)
    IOUtils.copy(new FileInputStream(path), aos, 8192)
    aos.closeArchiveEntry()
  }
}

zaos = new ZipArchiveOutputStream(new File("test.zip"))
zaos.setEncoding("Windows-31J")
basedir = new File("img")
writeEntries(zaos, basedir, basedir)
zaos.finish()
zaos.flush()
zaos.close()