# EFN 1.21.1 NeoForge Port Handoff

Дата: 2026-07-05

## Цель

Портировать закрытый мод **EpicFight Nightfall 3.4.0** с Minecraft 1.20.1 Forge на **Minecraft 1.21.1 NeoForge 21.1.235**.

Исходный jar:

```text
P:\FragmentPorting\EpicFight Nightfall-3.4.0.jar
```

Текущий рабочий проект:

```text
P:\FragmentPorting\nightfall-port
```

Текущий jar для Prism:

```text
P:\PrismLauncher\instances\test voxy\minecraft\mods\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar
```

## Зависимости

Используются версии под 1.21.1 NeoForge:

- Epic Fight: `epic-fight-21.17.3.1-mc1.21.1-neoforge.jar`
- Epic Fight - Invincible Lib
- Epic Fight - Avalon: `epic_fight_avalon-neoforge1.21.1-21.12.6.2.jar`
- AAA Particles: `aaa_particles-neoforge-1.21.1-2.2.0.jar`

Репозитории зависимостей лежат в:

```text
P:\FragmentPorting\deps
```

Важно: AAA Particles 2.2.0 подходит для MC 1.21.1 / NeoForge 21.1.235. Его metadata требует `minecraft [1.21, 1.21.2)` и `neoforge [21.1.169,)`. В логах он грузится нормально.

## Команды

Сборка:

```powershell
cd P:\FragmentPorting\nightfall-port
cmd /c "gradlew.bat --no-daemon clean build --console=plain > build.log 2>&1"
```

Копирование jar:

```powershell
Copy-Item -LiteralPath 'P:\FragmentPorting\nightfall-port\build\libs\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar' -Destination 'P:\PrismLauncher\instances\test voxy\minecraft\mods\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar' -Force
```

Запуск:

```powershell
Start-Process -FilePath 'P:\PrismLauncher\prismlauncher.exe' -ArgumentList '--launch "test voxy" --world "Новый мир"' -WindowStyle Hidden
```

Лог-проверка:

```powershell
Select-String -Path 'P:\PrismLauncher\instances\test voxy\minecraft\logs\latest.log' -Pattern '\[ERROR\]|\[FATAL\]|Critical injection failure|Mixin apply for mod voxy|Game crashed|InvalidInjection|Shader|compile|link|Creating Voxy render system|Voxy render system created'
```

Расширенная проверка:

```powershell
Select-String -Path 'P:\PrismLauncher\instances\test voxy\minecraft\logs\latest.log' -Pattern '\[ERROR\]|\[FATAL\]|Game crashed|ClassCastException|FriendlyByteBuf|RegistryFriendlyByteBuf|PacketBufferCodec|LongPressKeyHandler|Cannot invoke|NullPointerException|Unable to load model|Datapack animation reading failed|Item Capability Exception|Creating Voxy render system|Voxy render system created' | Select-Object -Last 800
```

## Что уже исправлено

### AAA warning

Старое предупреждение про AAA Particles было размыто блюром, потому что экран использовал `renderMenuBackground`. Исправлено в:

```text
src/main/java/com/hm/efn/client/gui/FirstLaunchWarningScreen.java
```

Теперь фон рисуется обычными темными `fill`, без blur.

Также `EFNClientConfig.setAAAWarningShown()` теперь сначала пишет marker-file и не падает, если NeoForge config еще не готов.

### Config spec crash

Был crash вида:

```text
Cannot get config value before spec is built
```

Исправлено переносом `SPEC = BUILDER.build()` в конец static init:

```text
src/main/java/com/hm/efn/EFNClientConfig.java
src/main/java/com/hm/efn/EFNCommonConfig.java
```

### Skill data registry

Был crash:

```text
DeferredHolder{ResourceKey[epicfight:skill_data_keys / efn:is_charging]} is unregistered
```

Причина: EFN создавал DeferredRegister старым способом через `ResourceLocation("epicfight", "skill_data_keys")`.

Исправлено в:

```text
src/main/java/com/hm/efn/gameasset/EFNSKillDataKeys.java
```

Теперь:

```java
DeferredRegister.create(EpicFightRegistries.SKILL_DATA_KEY, "efn")
```

После этого EpicFight начал видеть EFN data keys.

### RuinsGreatSword static animation crash

Был crash:

```text
RuinsGreatSwordInnate.FULL_CHARGE_ANIM is null
```

Причина: skill class кэшировал `EFNGreatSwordAnimations.*` в `private static final` до того, как `EFNAnimations.build(...)` заполнял accessors.

Исправлено в:

```text
src/main/java/com/hm/efn/skill/weapon_innate/RuinsGreatSwordInnate.java
```

Static-кэш заменен на lazy getters, добавлены null-safe проверки и helpers для `IS_CHARGING`, `IS_PRESSING`, `CHARGE_TICKS`.

### PacketBufferCodec crash на ЛКМ

Был crash при ЛКМ:

```text
ClassCastException: FriendlyByteBuf cannot be cast to RegistryFriendlyByteBuf
at com.hm.efn.compat.epicfight.utils.PacketBufferCodec.encode(PacketBufferCodec.java:7)
at yesman.epicfight.skill.SkillDataKey.encode(SkillDataKey.java:37)
at LongPressKeyHandler.syncKeyData(...)
```

Исправлено в:

```text
src/main/java/com/hm/efn/compat/epicfight/utils/PacketBufferCodec.java
```

Интерфейс теперь `StreamCodec<ByteBuf, T>`, а не `StreamCodec<RegistryFriendlyByteBuf, T>`. Для старых методов используется wrapper `new FriendlyByteBuf(buf)`.

После этого конкретный `FriendlyByteBuf` crash в логах больше не появился.

### MeenLance static animation crash

Последний проверенный runtime crash после codec-патча:

```text
MeenLanceInnate.FULL_CHARGE_ANIM is null
```

Внесена аналогичная правка в:

```text
src/main/java/com/hm/efn/skill/weapon_innate/MeenLanceInnate.java
```

Важно: эта последняя правка была внесена, но еще не прогонялась сборкой/запуском после решения завершить текущий чат.

## Текущее состояние

Игра доходит до мира, Voxy render system создается:

```text
Creating Voxy render system
Voxy render system created
```

Но боевка EFN еще не рабочая:

- ЛКМ не дает атаки.
- При смене/использовании другого оружия ловятся похожие проблемы.
- Вероятно, проблема системная для weapon innate/passive классов, а не только для одного оружия.

В логах также есть повторяющийся EpicFight client feedback NPE:

```text
Cannot invoke "yesman.epicfight.skill.Skill.executeOnClient(...)"
because SkillContainer.getSkill() is null
```

Это может объяснять отсутствие атаки на ЛКМ: клиент/сервер синхронизирует feedback для слота, где skill не установлен. Нужно проверить регистрацию EFN skills, item capabilities и datapack combo/weapon capability binding.

## Главные следующие задачи

1. Собрать после последней правки `MeenLanceInnate`.

2. Прогнать запуск и проверить, исчез ли:

```text
MeenLanceInnate.FULL_CHARGE_ANIM is null
```

3. Системно найти все ранние static animation caches:

```powershell
rg -n "private static final AnimationAccessor|static final AnimationAccessor|AnimationAccessor<\\? extends .* = EFN" src\main\java\com\hm\efn\skill
```

Особенно уже видно:

```text
src/main/java/com/hm/efn/skill/weapon_innate/YamatoInnate.java
```

Там такие же поля:

```java
JUDGECUT_ANIM = EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL
QUICK_ANIM = EFNYamatoAnimations.YAMATO_JUDEMENCUT
JUST_ANIM = EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST
CHARGE_ANIM = EFNYamatoAnimations.YAMATO_JUDEMENCUT_CHARGE
```

Их нужно перевести на lazy getters/null-safe comparison, как в RuinsGreatSword/MeenLance.

4. Разобрать `SkillContainer.getSkill() is null`.

Начать с:

```text
src/main/java/com/hm/efn/gameasset/EFNSkills.java
src/main/java/com/hm/efn/gameasset/combos/*.java
src/main/java/com/hm/efn/gameasset/EFNWeaponCapabilityPresets.java
src/main/java/com/hm/efn/compat/epicfight/forgeevent/SkillBuildEvent.java
```

Сравнивать с EpicFight/Invincible/Avalon 1.21.1 registry patterns.

5. Проверить item capability binding. В логах есть:

```text
Item Capability Exception: No item named efn:scythe
Pulling epicfight:greatsword from register
```

Если capabilities не мапятся на EFN items, EpicFight может не ставить нужный innate skill, и ЛКМ будет пустой.

6. Починить модели предметов.

Сейчас Minecraft не находит обычные item model json для части зарегистрированных предметов:

```text
efn:models/item/crescent_moon_e.json
efn:models/item/meen_spear_e.json
efn:models/item/fire_exsiliumgladius_e.json
efn:models/item/crimson_moon_e.json
efn:models/item/arc_tachi.json
efn:models/item/nf_shortsword_2_e.json
efn:models/item/nf_shortsword_e.json
efn:models/item/flag_bearer_e.json
efn:models/item/exsiliumgladius_e.json
efn:models/item/flag_bearer.json
efn:models/item/air_tachi_e.json
```

Для base variants json есть, для `_e` variants часто нет. Можно временно создать vanilla generated item models, указывающие на существующие текстуры, чтобы убрать missing model. Для 3D held model нужно отдельно проверить Avalon `item_skins`.

Пример существующего item skin:

```text
src/main/resources/assets/efn/item_skins/ruinsgreatsword.json
```

Он использует:

```json
"renderer": "epic_fight_avalon:mesh_item",
"mesh_main": "efn:weapon/ruinsgreatsword"
```

Если 3D модель не подтягивается, смотреть Avalon mesh item loader и путь `assets/efn/item_skins/*.json`.

7. Разобрать ошибки datapack animations:

```text
Datapack animation reading failed: No constructor information has provided: efn:...
```

Это может быть несовместимость формата animation json с EpicFight 1.21.1 или недостающая регистрация custom animation constructors из Avalon/EFN.

## Важные текущие подозрения

### Не единичный баг оружия

Похоже, что почти все оружие EFN может страдать от одной из трех системных проблем:

- ранний static-кэш animation accessors до `EFNAnimations.build(...)`;
- item capabilities/combos не привязались к registered EFN items;
- EpicFight skill feedback приходит в пустой SkillContainer.

### ЛКМ

`FriendlyByteBuf` crash на ЛКМ уже исправлен, но атака все еще не происходит. Следующий фокус: почему basic attack или weapon innate skill не назначается в EpicFight item capability.

### Модели

Визуально "3D модель не подтянулась" может иметь две разные причины:

- vanilla item model json отсутствует, поэтому item broken/missing;
- Avalon mesh item renderer не видит `item_skins` или mesh path.

Нужно отделить inventory model от held/combat model.

## Измененные файлы в этом этапе

```text
src/main/java/com/hm/efn/EFNClientConfig.java
src/main/java/com/hm/efn/EFNCommonConfig.java
src/main/java/com/hm/efn/client/gui/FirstLaunchWarningScreen.java
src/main/java/com/hm/efn/gameasset/EFNSKillDataKeys.java
src/main/java/com/hm/efn/compat/epicfight/utils/PacketBufferCodec.java
src/main/java/com/hm/efn/skill/weapon_innate/RuinsGreatSwordInnate.java
src/main/java/com/hm/efn/skill/weapon_innate/MeenLanceInnate.java
```

Также раньше в проект уже были добавлены legacy compatibility/facade классы для EpicFight/VIX/Avalon. Не откатывать их без проверки.

## Минимальный старт в новом чате

1. Открыть этот файл.
2. Выполнить сборку.
3. Скопировать jar в Prism mods.
4. Запустить мир.
5. Смотреть последний crash.
6. Если следующий crash снова `SOME_ANIM is null`, чинить такой же lazy-getter схемой.
7. Если null-анимации ушли, заняться `SkillContainer.getSkill() is null` и item capabilities.

