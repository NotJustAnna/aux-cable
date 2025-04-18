.version 61 0
.class public final super io/micronaut/serde/support/serdes/Serdes
.super java/lang/Object
.field public static final INTEGER_SERDE Lio/micronaut/serde/support/serdes/IntegerSerde;
.field public static final LONG_SERDE Lio/micronaut/serde/support/serdes/LongSerde;
.field public static final SHORT_SERDE Lio/micronaut/serde/support/serdes/ShortSerde;
.field public static final FLOAT_SERDE Lio/micronaut/serde/support/serdes/FloatSerde;
.field public static final BYTE_SERDE Lio/micronaut/serde/support/serdes/ByteSerde;
.field public static final DOUBLE_SERDE Lio/micronaut/serde/support/serdes/DoubleSerde;
.field public static final OPTIONAL_INT_SERDE Lio/micronaut/serde/support/serdes/OptionalIntSerde;
.field public static final OPTIONAL_DOUBLE_SERDE Lio/micronaut/serde/support/serdes/OptionalDoubleSerde;
.field public static final OPTIONAL_LONG_SERDE Lio/micronaut/serde/support/serdes/OptionalLongSerde;
.field public static final BIG_DECIMAL_SERDE Lio/micronaut/serde/support/serdes/BigDecimalSerde;
.field public static final BIG_INTEGER_SERDE Lio/micronaut/serde/support/serdes/BigIntegerSerde;
.field public static final UUID_SERDE Lio/micronaut/serde/support/serdes/UUIDSerde;
.field public static final URL_SERDE Lio/micronaut/serde/support/serdes/URLSerde;
.field public static final URI_SERDE Lio/micronaut/serde/support/serdes/URISerde;
.field public static final CHARSET_SERDE Lio/micronaut/serde/support/serdes/CharsetSerde;
.field public static final TIME_ZONE_SERDE Lio/micronaut/serde/support/serdes/TimeZoneSerde;
.field public static final LOCALE_SERDE Lio/micronaut/serde/support/serdes/LocaleSerde;
.field public static final INT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/IntArraySerde;
.field public static final LONG_ARRAY_SERDE Lio/micronaut/serde/support/serdes/LongArraySerde;
.field public static final FLOAT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/FloatArraySerde;
.field public static final SHORT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/ShortArraySerde;
.field public static final DOUBLE_ARRAY_SERDE Lio/micronaut/serde/support/serdes/DoubleArraySerde;
.field public static final BOOLEAN_ARRAY_SERDE Lio/micronaut/serde/support/serdes/BooleanArraySerde;
.field public static final BYTE_ARRAY_SERDE Lio/micronaut/serde/support/serdes/ByteArraySerde;
.field public static final CHAR_ARRAY_SERDE Lio/micronaut/serde/support/serdes/CharArraySerde;
.field public static final STRING_SERDE Lio/micronaut/serde/support/serdes/StringSerde;
.field public static final BOOLEAN_SERDE Lio/micronaut/serde/support/serdes/BooleanSerde;
.field public static final CHAR_SERDE Lio/micronaut/serde/support/serdes/CharSerde;
.field public static final LEGACY_DEFAULT_SERDES Ljava/util/List; .fieldattributes
    .signature Ljava/util/List<Lio/micronaut/serde/support/SerdeRegistrar<*>;>;
.end fieldattributes
.field private static final SERDES Ljava/util/List; .fieldattributes
    .signature Ljava/util/List<Lio/micronaut/serde/support/SerdeRegistrar<*>;>;
.end fieldattributes

.method public <init> : ()V
    .code stack 1 locals 1
L0:     aload_0
L1:     invokespecial Method java/lang/Object <init> ()V
L4:     return
L5:     
        .linenumbertable
            L0 33
        .end linenumbertable
        .localvariabletable
            0 is this Lio/micronaut/serde/support/serdes/Serdes; from L0 to L5
        .end localvariabletable
    .end code
.end method

.method public static register : (Lio/micronaut/serde/config/SerdeConfiguration;Lio/micronaut/serde/SerdeIntrospections;Ljava/util/function/Consumer;)V
    .code stack 4 locals 7
L0:     getstatic Field io/micronaut/serde/support/serdes/Serdes LEGACY_DEFAULT_SERDES Ljava/util/List;
L3:     aload_2
L4:     invokeinterface InterfaceMethod java/util/List forEach (Ljava/util/function/Consumer;)V 2
L9:     aload_2
L10:    new io/micronaut/serde/support/serdes/ByteArraySerde
L13:    dup
L14:    aload_0
L15:    invokespecial Method io/micronaut/serde/support/serdes/ByteArraySerde <init> (Lio/micronaut/serde/config/SerdeConfiguration;)V
L18:    invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L23:    getstatic Field io/micronaut/serde/support/serdes/Serdes SERDES Ljava/util/List;
L26:    aload_2
L27:    invokeinterface InterfaceMethod java/util/List forEach (Ljava/util/function/Consumer;)V 2
L32:    new io/micronaut/serde/support/serdes/InstantSerde
L35:    dup
L36:    aload_0
L37:    invokespecial Method io/micronaut/serde/support/serdes/InstantSerde <init> (Lio/micronaut/serde/config/SerdeConfiguration;)V
L40:    astore_3
L41:    aload_2
L42:    aload_3
L43:    invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L48:    aload_2
L49:    new io/micronaut/serde/support/serdes/DateSerde
L52:    dup
L53:    aload_3
L54:    invokespecial Method io/micronaut/serde/support/serdes/DateSerde <init> (Lio/micronaut/serde/support/serdes/InstantSerde;)V
L57:    invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L62:    new io/micronaut/serde/support/serdes/LocalDateSerde
L65:    dup
L66:    aload_0
L67:    invokespecial Method io/micronaut/serde/support/serdes/LocalDateSerde <init> (Lio/micronaut/serde/config/SerdeConfiguration;)V
L70:    astore 4
L72:    aload_2
L73:    aload 4
L75:    invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L80:    aload_2
L81:    new io/micronaut/serde/support/serdes/LocalTimeSerde
L84:    dup
L85:    aload_0
L86:    invokespecial Method io/micronaut/serde/support/serdes/LocalTimeSerde <init> (Lio/micronaut/serde/config/SerdeConfiguration;)V
L89:    invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L94:    aload_2
L95:    new io/micronaut/serde/support/serdes/LocalDateTimeSerde
L98:    dup
L99:    aload_0
L100:   invokespecial Method io/micronaut/serde/support/serdes/LocalDateTimeSerde <init> (Lio/micronaut/serde/config/SerdeConfiguration;)V
L103:   invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L108:   aload_2
L109:   new io/micronaut/serde/support/serdes/OffsetDateTimeSerde
L112:   dup
L113:   aload_0
L114:   invokespecial Method io/micronaut/serde/support/serdes/OffsetDateTimeSerde <init> (Lio/micronaut/serde/config/SerdeConfiguration;)V
L117:   invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L151:   aload_2
L152:   new io/micronaut/serde/support/serdes/YearSerde
L155:   dup
L156:   invokespecial Method io/micronaut/serde/support/serdes/YearSerde <init> ()V
L159:   invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L164:   aload_2
L165:   new io/micronaut/serde/support/serdes/ZonedDateTimeSerde
L168:   dup
L169:   aload_0
L170:   invokespecial Method io/micronaut/serde/support/serdes/ZonedDateTimeSerde <init> (Lio/micronaut/serde/config/SerdeConfiguration;)V
L173:   invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L178:   aload_2
L179:   new io/micronaut/serde/support/serdes/EnumSerde
L182:   dup
L183:   aload_1
L184:   invokespecial Method io/micronaut/serde/support/serdes/EnumSerde <init> (Lio/micronaut/serde/SerdeIntrospections;)V
L187:   invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
L192:   aload_2
L193:   new io/micronaut/serde/support/serdes/InetAddressSerde
L196:   dup
L197:   aload_0
L198:   invokespecial Method io/micronaut/serde/support/serdes/InetAddressSerde <init> (Lio/micronaut/serde/config/SerdeConfiguration;)V
L201:   invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2
        .catch java/lang/LinkageError from L206 to L215 using L218
L206:   new io/micronaut/serde/support/serdes/JacksonJsonNodeSerde
L209:   dup
L210:   invokespecial Method io/micronaut/serde/support/serdes/JacksonJsonNodeSerde <init> ()V
L213:   astore 5
L215:   goto L223

        .stack full
            locals Object io/micronaut/serde/config/SerdeConfiguration Object io/micronaut/serde/SerdeIntrospections Object java/util/function/Consumer Object io/micronaut/serde/support/serdes/InstantSerde Object io/micronaut/serde/support/serdes/LocalDateSerde
            stack Object java/lang/LinkageError
        .end stack
L218:   astore 6
L220:   aconst_null
L221:   astore 5

        .stack append Object io/micronaut/serde/support/SerdeRegistrar
L223:   aload 5
L225:   ifnull L236
L228:   aload_2
L229:   aload 5
L231:   invokeinterface InterfaceMethod java/util/function/Consumer accept (Ljava/lang/Object;)V 2

        .stack same
L236:   return
L237:   
        .linenumbertable
            L0 108
            L9 109
            L23 110
            L32 111
            L41 112
            L48 113
            L62 114
            L72 115
            L80 116
            L94 117
            L108 118
            L151 121
            L164 122
            L178 123
            L192 124
            L206 127
            L215 130
            L218 128
            L220 129
            L223 131
            L228 132
            L236 134
        .end linenumbertable
        .localvariabletable
            5 is jacksonJsonNodeSerde Lio/micronaut/serde/support/SerdeRegistrar; from L215 to L218
            6 is ignored Ljava/lang/LinkageError; from L220 to L223
            0 is serdeConfiguration Lio/micronaut/serde/config/SerdeConfiguration; from L0 to L237
            1 is introspections Lio/micronaut/serde/SerdeIntrospections; from L0 to L237
            2 is consumer Ljava/util/function/Consumer; from L0 to L237
            3 is instantSerde Lio/micronaut/serde/support/serdes/InstantSerde; from L41 to L237
            4 is localDateSerde Lio/micronaut/serde/support/serdes/LocalDateSerde; from L72 to L237
            5 is jacksonJsonNodeSerde Lio/micronaut/serde/support/SerdeRegistrar; from L223 to L237
        .end localvariabletable
        .localvariabletypetable
            5 is jacksonJsonNodeSerde Lio/micronaut/serde/support/SerdeRegistrar<*>; from L215 to L218
            2 is consumer Ljava/util/function/Consumer<Lio/micronaut/serde/support/SerdeRegistrar<*>;>; from L0 to L237
            5 is jacksonJsonNodeSerde Lio/micronaut/serde/support/SerdeRegistrar<*>; from L223 to L237
        .end localvariabletypetable
    .end code
    .methodparameters
        serdeConfiguration
        introspections
        consumer
    .end methodparameters
    .signature (Lio/micronaut/serde/config/SerdeConfiguration;Lio/micronaut/serde/SerdeIntrospections;Ljava/util/function/Consumer<Lio/micronaut/serde/support/SerdeRegistrar<*>;>;)V
.end method

.method static <clinit> : ()V
    .code stack 8 locals 0
L0:     new io/micronaut/serde/support/serdes/IntegerSerde
L3:     dup
L4:     invokespecial Method io/micronaut/serde/support/serdes/IntegerSerde <init> ()V
L7:     putstatic Field io/micronaut/serde/support/serdes/Serdes INTEGER_SERDE Lio/micronaut/serde/support/serdes/IntegerSerde;
L10:    new io/micronaut/serde/support/serdes/LongSerde
L13:    dup
L14:    invokespecial Method io/micronaut/serde/support/serdes/LongSerde <init> ()V
L17:    putstatic Field io/micronaut/serde/support/serdes/Serdes LONG_SERDE Lio/micronaut/serde/support/serdes/LongSerde;
L20:    new io/micronaut/serde/support/serdes/ShortSerde
L23:    dup
L24:    invokespecial Method io/micronaut/serde/support/serdes/ShortSerde <init> ()V
L27:    putstatic Field io/micronaut/serde/support/serdes/Serdes SHORT_SERDE Lio/micronaut/serde/support/serdes/ShortSerde;
L30:    new io/micronaut/serde/support/serdes/FloatSerde
L33:    dup
L34:    invokespecial Method io/micronaut/serde/support/serdes/FloatSerde <init> ()V
L37:    putstatic Field io/micronaut/serde/support/serdes/Serdes FLOAT_SERDE Lio/micronaut/serde/support/serdes/FloatSerde;
L40:    new io/micronaut/serde/support/serdes/ByteSerde
L43:    dup
L44:    invokespecial Method io/micronaut/serde/support/serdes/ByteSerde <init> ()V
L47:    putstatic Field io/micronaut/serde/support/serdes/Serdes BYTE_SERDE Lio/micronaut/serde/support/serdes/ByteSerde;
L50:    new io/micronaut/serde/support/serdes/DoubleSerde
L53:    dup
L54:    invokespecial Method io/micronaut/serde/support/serdes/DoubleSerde <init> ()V
L57:    putstatic Field io/micronaut/serde/support/serdes/Serdes DOUBLE_SERDE Lio/micronaut/serde/support/serdes/DoubleSerde;
L60:    new io/micronaut/serde/support/serdes/OptionalIntSerde
L63:    dup
L64:    invokespecial Method io/micronaut/serde/support/serdes/OptionalIntSerde <init> ()V
L67:    putstatic Field io/micronaut/serde/support/serdes/Serdes OPTIONAL_INT_SERDE Lio/micronaut/serde/support/serdes/OptionalIntSerde;
L70:    new io/micronaut/serde/support/serdes/OptionalDoubleSerde
L73:    dup
L74:    invokespecial Method io/micronaut/serde/support/serdes/OptionalDoubleSerde <init> ()V
L77:    putstatic Field io/micronaut/serde/support/serdes/Serdes OPTIONAL_DOUBLE_SERDE Lio/micronaut/serde/support/serdes/OptionalDoubleSerde;
L80:    new io/micronaut/serde/support/serdes/OptionalLongSerde
L83:    dup
L84:    invokespecial Method io/micronaut/serde/support/serdes/OptionalLongSerde <init> ()V
L87:    putstatic Field io/micronaut/serde/support/serdes/Serdes OPTIONAL_LONG_SERDE Lio/micronaut/serde/support/serdes/OptionalLongSerde;
L90:    new io/micronaut/serde/support/serdes/BigDecimalSerde
L93:    dup
L94:    invokespecial Method io/micronaut/serde/support/serdes/BigDecimalSerde <init> ()V
L97:    putstatic Field io/micronaut/serde/support/serdes/Serdes BIG_DECIMAL_SERDE Lio/micronaut/serde/support/serdes/BigDecimalSerde;
L100:   new io/micronaut/serde/support/serdes/BigIntegerSerde
L103:   dup
L104:   invokespecial Method io/micronaut/serde/support/serdes/BigIntegerSerde <init> ()V
L107:   putstatic Field io/micronaut/serde/support/serdes/Serdes BIG_INTEGER_SERDE Lio/micronaut/serde/support/serdes/BigIntegerSerde;
L110:   new io/micronaut/serde/support/serdes/UUIDSerde
L113:   dup
L114:   invokespecial Method io/micronaut/serde/support/serdes/UUIDSerde <init> ()V
L117:   putstatic Field io/micronaut/serde/support/serdes/Serdes UUID_SERDE Lio/micronaut/serde/support/serdes/UUIDSerde;
L120:   new io/micronaut/serde/support/serdes/URLSerde
L123:   dup
L124:   invokespecial Method io/micronaut/serde/support/serdes/URLSerde <init> ()V
L127:   putstatic Field io/micronaut/serde/support/serdes/Serdes URL_SERDE Lio/micronaut/serde/support/serdes/URLSerde;
L130:   new io/micronaut/serde/support/serdes/URISerde
L133:   dup
L134:   invokespecial Method io/micronaut/serde/support/serdes/URISerde <init> ()V
L137:   putstatic Field io/micronaut/serde/support/serdes/Serdes URI_SERDE Lio/micronaut/serde/support/serdes/URISerde;
L140:   new io/micronaut/serde/support/serdes/CharsetSerde
L143:   dup
L144:   invokespecial Method io/micronaut/serde/support/serdes/CharsetSerde <init> ()V
L147:   putstatic Field io/micronaut/serde/support/serdes/Serdes CHARSET_SERDE Lio/micronaut/serde/support/serdes/CharsetSerde;
L150:   new io/micronaut/serde/support/serdes/TimeZoneSerde
L153:   dup
L154:   invokespecial Method io/micronaut/serde/support/serdes/TimeZoneSerde <init> ()V
L157:   putstatic Field io/micronaut/serde/support/serdes/Serdes TIME_ZONE_SERDE Lio/micronaut/serde/support/serdes/TimeZoneSerde;
L160:   new io/micronaut/serde/support/serdes/LocaleSerde
L163:   dup
L164:   invokespecial Method io/micronaut/serde/support/serdes/LocaleSerde <init> ()V
L167:   putstatic Field io/micronaut/serde/support/serdes/Serdes LOCALE_SERDE Lio/micronaut/serde/support/serdes/LocaleSerde;
L170:   new io/micronaut/serde/support/serdes/IntArraySerde
L173:   dup
L174:   invokespecial Method io/micronaut/serde/support/serdes/IntArraySerde <init> ()V
L177:   putstatic Field io/micronaut/serde/support/serdes/Serdes INT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/IntArraySerde;
L180:   new io/micronaut/serde/support/serdes/LongArraySerde
L183:   dup
L184:   invokespecial Method io/micronaut/serde/support/serdes/LongArraySerde <init> ()V
L187:   putstatic Field io/micronaut/serde/support/serdes/Serdes LONG_ARRAY_SERDE Lio/micronaut/serde/support/serdes/LongArraySerde;
L190:   new io/micronaut/serde/support/serdes/FloatArraySerde
L193:   dup
L194:   invokespecial Method io/micronaut/serde/support/serdes/FloatArraySerde <init> ()V
L197:   putstatic Field io/micronaut/serde/support/serdes/Serdes FLOAT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/FloatArraySerde;
L200:   new io/micronaut/serde/support/serdes/ShortArraySerde
L203:   dup
L204:   invokespecial Method io/micronaut/serde/support/serdes/ShortArraySerde <init> ()V
L207:   putstatic Field io/micronaut/serde/support/serdes/Serdes SHORT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/ShortArraySerde;
L210:   new io/micronaut/serde/support/serdes/DoubleArraySerde
L213:   dup
L214:   invokespecial Method io/micronaut/serde/support/serdes/DoubleArraySerde <init> ()V
L217:   putstatic Field io/micronaut/serde/support/serdes/Serdes DOUBLE_ARRAY_SERDE Lio/micronaut/serde/support/serdes/DoubleArraySerde;
L220:   new io/micronaut/serde/support/serdes/BooleanArraySerde
L223:   dup
L224:   invokespecial Method io/micronaut/serde/support/serdes/BooleanArraySerde <init> ()V
L227:   putstatic Field io/micronaut/serde/support/serdes/Serdes BOOLEAN_ARRAY_SERDE Lio/micronaut/serde/support/serdes/BooleanArraySerde;
L230:   new io/micronaut/serde/support/serdes/ByteArraySerde
L233:   dup
L234:   iconst_1
L235:   invokespecial Method io/micronaut/serde/support/serdes/ByteArraySerde <init> (Z)V
L238:   putstatic Field io/micronaut/serde/support/serdes/Serdes BYTE_ARRAY_SERDE Lio/micronaut/serde/support/serdes/ByteArraySerde;
L241:   new io/micronaut/serde/support/serdes/CharArraySerde
L244:   dup
L245:   invokespecial Method io/micronaut/serde/support/serdes/CharArraySerde <init> ()V
L248:   putstatic Field io/micronaut/serde/support/serdes/Serdes CHAR_ARRAY_SERDE Lio/micronaut/serde/support/serdes/CharArraySerde;
L251:   new io/micronaut/serde/support/serdes/StringSerde
L254:   dup
L255:   invokespecial Method io/micronaut/serde/support/serdes/StringSerde <init> ()V
L258:   putstatic Field io/micronaut/serde/support/serdes/Serdes STRING_SERDE Lio/micronaut/serde/support/serdes/StringSerde;
L261:   new io/micronaut/serde/support/serdes/BooleanSerde
L264:   dup
L265:   invokespecial Method io/micronaut/serde/support/serdes/BooleanSerde <init> ()V
L268:   putstatic Field io/micronaut/serde/support/serdes/Serdes BOOLEAN_SERDE Lio/micronaut/serde/support/serdes/BooleanSerde;
L271:   new io/micronaut/serde/support/serdes/CharSerde
L274:   dup
L275:   invokespecial Method io/micronaut/serde/support/serdes/CharSerde <init> ()V
L278:   putstatic Field io/micronaut/serde/support/serdes/Serdes CHAR_SERDE Lio/micronaut/serde/support/serdes/CharSerde;
L281:   bipush 27
L283:   anewarray io/micronaut/serde/support/SerdeRegistrar
L286:   dup
L287:   iconst_0
L288:   getstatic Field io/micronaut/serde/support/serdes/Serdes BOOLEAN_SERDE Lio/micronaut/serde/support/serdes/BooleanSerde;
L291:   aastore
L292:   dup
L293:   iconst_1
L294:   getstatic Field io/micronaut/serde/support/serdes/Serdes BYTE_SERDE Lio/micronaut/serde/support/serdes/ByteSerde;
L297:   aastore
L298:   dup
L299:   iconst_2
L300:   getstatic Field io/micronaut/serde/support/serdes/Serdes CHAR_SERDE Lio/micronaut/serde/support/serdes/CharSerde;
L303:   aastore
L304:   dup
L305:   iconst_3
L306:   getstatic Field io/micronaut/serde/support/serdes/Serdes DOUBLE_SERDE Lio/micronaut/serde/support/serdes/DoubleSerde;
L309:   aastore
L310:   dup
L311:   iconst_4
L312:   getstatic Field io/micronaut/serde/support/serdes/Serdes FLOAT_SERDE Lio/micronaut/serde/support/serdes/FloatSerde;
L315:   aastore
L316:   dup
L317:   iconst_5
L318:   getstatic Field io/micronaut/serde/support/serdes/Serdes INTEGER_SERDE Lio/micronaut/serde/support/serdes/IntegerSerde;
L321:   aastore
L322:   dup
L323:   bipush 6
L325:   getstatic Field io/micronaut/serde/support/serdes/Serdes LONG_SERDE Lio/micronaut/serde/support/serdes/LongSerde;
L328:   aastore
L329:   dup
L330:   bipush 7
L332:   getstatic Field io/micronaut/serde/support/serdes/Serdes SHORT_SERDE Lio/micronaut/serde/support/serdes/ShortSerde;
L335:   aastore
L336:   dup
L337:   bipush 8
L339:   getstatic Field io/micronaut/serde/support/serdes/Serdes STRING_SERDE Lio/micronaut/serde/support/serdes/StringSerde;
L342:   aastore
L343:   dup
L344:   bipush 9
L346:   getstatic Field io/micronaut/serde/support/serdes/Serdes OPTIONAL_INT_SERDE Lio/micronaut/serde/support/serdes/OptionalIntSerde;
L349:   aastore
L350:   dup
L351:   bipush 10
L353:   getstatic Field io/micronaut/serde/support/serdes/Serdes OPTIONAL_DOUBLE_SERDE Lio/micronaut/serde/support/serdes/OptionalDoubleSerde;
L356:   aastore
L357:   dup
L358:   bipush 11
L360:   getstatic Field io/micronaut/serde/support/serdes/Serdes OPTIONAL_LONG_SERDE Lio/micronaut/serde/support/serdes/OptionalLongSerde;
L363:   aastore
L364:   dup
L365:   bipush 12
L367:   getstatic Field io/micronaut/serde/support/serdes/Serdes BIG_DECIMAL_SERDE Lio/micronaut/serde/support/serdes/BigDecimalSerde;
L370:   aastore
L371:   dup
L372:   bipush 13
L374:   getstatic Field io/micronaut/serde/support/serdes/Serdes BIG_INTEGER_SERDE Lio/micronaut/serde/support/serdes/BigIntegerSerde;
L377:   aastore
L378:   dup
L379:   bipush 14
L381:   getstatic Field io/micronaut/serde/support/serdes/Serdes UUID_SERDE Lio/micronaut/serde/support/serdes/UUIDSerde;
L384:   aastore
L385:   dup
L386:   bipush 15
L388:   getstatic Field io/micronaut/serde/support/serdes/Serdes URL_SERDE Lio/micronaut/serde/support/serdes/URLSerde;
L391:   aastore
L392:   dup
L393:   bipush 16
L395:   getstatic Field io/micronaut/serde/support/serdes/Serdes URI_SERDE Lio/micronaut/serde/support/serdes/URISerde;
L398:   aastore
L399:   dup
L400:   bipush 17
L402:   getstatic Field io/micronaut/serde/support/serdes/Serdes CHARSET_SERDE Lio/micronaut/serde/support/serdes/CharsetSerde;
L405:   aastore
L406:   dup
L407:   bipush 18
L409:   getstatic Field io/micronaut/serde/support/serdes/Serdes TIME_ZONE_SERDE Lio/micronaut/serde/support/serdes/TimeZoneSerde;
L412:   aastore
L413:   dup
L414:   bipush 19
L416:   getstatic Field io/micronaut/serde/support/serdes/Serdes LOCALE_SERDE Lio/micronaut/serde/support/serdes/LocaleSerde;
L419:   aastore
L420:   dup
L421:   bipush 20
L423:   getstatic Field io/micronaut/serde/support/serdes/Serdes INT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/IntArraySerde;
L426:   aastore
L427:   dup
L428:   bipush 21
L430:   getstatic Field io/micronaut/serde/support/serdes/Serdes LONG_ARRAY_SERDE Lio/micronaut/serde/support/serdes/LongArraySerde;
L433:   aastore
L434:   dup
L435:   bipush 22
L437:   getstatic Field io/micronaut/serde/support/serdes/Serdes FLOAT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/FloatArraySerde;
L440:   aastore
L441:   dup
L442:   bipush 23
L444:   getstatic Field io/micronaut/serde/support/serdes/Serdes SHORT_ARRAY_SERDE Lio/micronaut/serde/support/serdes/ShortArraySerde;
L447:   aastore
L448:   dup
L449:   bipush 24
L451:   getstatic Field io/micronaut/serde/support/serdes/Serdes DOUBLE_ARRAY_SERDE Lio/micronaut/serde/support/serdes/DoubleArraySerde;
L454:   aastore
L455:   dup
L456:   bipush 25
L458:   getstatic Field io/micronaut/serde/support/serdes/Serdes BOOLEAN_ARRAY_SERDE Lio/micronaut/serde/support/serdes/BooleanArraySerde;
L461:   aastore
L462:   dup
L463:   bipush 26
L465:   getstatic Field io/micronaut/serde/support/serdes/Serdes CHAR_ARRAY_SERDE Lio/micronaut/serde/support/serdes/CharArraySerde;
L468:   aastore
L469:   invokestatic InterfaceMethod java/util/List of ([Ljava/lang/Object;)Ljava/util/List;
L472:   putstatic Field io/micronaut/serde/support/serdes/Serdes LEGACY_DEFAULT_SERDES Ljava/util/List;
L475:   new io/micronaut/serde/support/serdes/DurationSerde
L478:   dup
L479:   invokespecial Method io/micronaut/serde/support/serdes/DurationSerde <init> ()V
L482:   new io/micronaut/serde/support/serdes/JsonNodeSerde
L485:   dup
L486:   invokespecial Method io/micronaut/serde/support/serdes/JsonNodeSerde <init> ()V
L489:   new io/micronaut/serde/support/serdes/PeriodSerde
L492:   dup
L493:   invokespecial Method io/micronaut/serde/support/serdes/PeriodSerde <init> ()V
L496:   new io/micronaut/serde/support/serdes/ByteBufferSerde
L499:   dup
L500:   invokespecial Method io/micronaut/serde/support/serdes/ByteBufferSerde <init> ()V
L503:   new io/micronaut/serde/support/serdes/StringArraySerde
L506:   dup
L507:   invokespecial Method io/micronaut/serde/support/serdes/StringArraySerde <init> ()V
L510:   new io/micronaut/serde/support/serdes/OptionalSerde
L513:   dup
L514:   invokespecial Method io/micronaut/serde/support/serdes/OptionalSerde <init> ()V
L517:   new io/micronaut/serde/support/serdes/NumberTypeSerde
L520:   dup
L521:   invokespecial Method io/micronaut/serde/support/serdes/NumberTypeSerde <init> ()V
L524:   invokestatic InterfaceMethod java/util/List of (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;
L527:   putstatic Field io/micronaut/serde/support/serdes/Serdes SERDES Ljava/util/List;
L530:   return
L531:   
        .linenumbertable
            L0 35
            L10 36
            L20 37
            L30 38
            L40 39
            L50 40
            L60 41
            L70 42
            L80 43
            L90 44
            L100 45
            L110 46
            L120 47
            L130 48
            L140 49
            L150 50
            L160 51
            L170 52
            L180 53
            L190 54
            L200 55
            L210 56
            L220 57
            L230 58
            L241 59
            L251 61
            L261 63
            L271 64
            L281 65
            L475 95
        .end linenumbertable
    .end code
.end method
.sourcefile "Serdes.java"
.runtime visible annotations
    .annotation Lio/micronaut/core/annotation/Internal;
    .end annotation
.end runtime
.end class
