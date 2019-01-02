package chela.kotlin.regex

object reK: ChRegex("""^\s*(?:"([^":]*)"|([^:,\s"`]+)|`([^`:]*)`)\s*:\s*""")