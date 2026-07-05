# EFN 1.21.1 NeoForge Port Handoff

Дата: 2026-07-06

## Цель

Портировать закрытый мод EpicFight Nightfall 3.4.0 с Minecraft 1.20.1 Forge на Minecraft 1.21.1 NeoForge 21.1.235.

Текущий репозиторий порта:

```text
P:\FragmentPorting\nightfall-port
```

Ветка:

```text
dev-0.0.1
```

Тестовый Prism instance:

```text
P:\PrismLauncher\instances\test voxy
```

## Текущий baseline

Состояние после очистки: экспериментальные хуки вокруг ЛКМ, Epic Fight input, Invincible mouse input и временная трассировка удалены. Это не означает, что атаки починены. Это означает, что порт возвращен к более честной базе без моих поздних костылей, которые мешали понять реальную причину.

Удалены/откачены такие временные файлы и регистрации:

```text
src/main/java/com/hm/efn/client/input/VanillaAttackInputFallback.java
src/main/java/com/hm/efn/util/EFNBasicAttackRouting.java
src/main/java/com/hm/efn/util/EFNInputKeyUtil.java
src/main/java/com/hm/efn/mixin/ComboAttacksTraceMixin.java
src/main/java/com/hm/efn/mixin/ControlEngineInvoker.java
src/main/java/com/hm/efn/mixin/ControlEnginePrimaryAttackMixin.java
src/main/java/com/hm/efn/mixin/EpicFightClientBoundPayloadHandlerMixin.java
src/main/java/com/hm/efn/mixin/EpicFightDiscreteInputActionTriggerMixin.java
src/main/java/com/hm/efn/mixin/EpicFightInputManagerMixin.java
src/main/java/com/hm/efn/mixin/EpicFightServerBoundPayloadHandlerMixin.java
```

`EFNWeaponInnateBase` возвращен к простой форме, как в декомпилированном Nightfall:

```text
P:\FragmentPorting\nightfall-port\src\main\java\com\hm\efn\skill\EFNWeaponInnateBase.java
```

Сборка после очистки проходит:

```powershell
Set-Location 'P:\FragmentPorting\nightfall-port'
.\gradlew build
```

Последний jar, установленный в `test voxy`, совпадает с build-output:

```text
SHA256: BA41B6CFFAE9F28C86410DB2A654917BB8E1D91B465EC667C923A4FA2493455A
```

## Где лежит Nightfall

Оригинальный закрытый jar 1.20.1:

```text
P:\FragmentPorting\EpicFight Nightfall-3.4.0.jar
```

Декомпилированная версия Nightfall:

```text
P:\FragmentPorting\decompiled\nightfall-vineflower
```

Рабочий порт на 1.21.1 NeoForge:

```text
P:\FragmentPorting\nightfall-port
```

Собранный jar порта:

```text
P:\FragmentPorting\nightfall-port\build\libs\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar
```

Установленный jar в Prism:

```text
P:\PrismLauncher\instances\test voxy\minecraft\mods\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar
```

## Где лежат исходники зависимостей

Epic Fight:

```text
P:\FragmentPorting\deps\epicfight-1.20.1
P:\FragmentPorting\deps\epicfight-1.21.1
```

Важные классы Epic Fight для следующего расследования:

```text
P:\FragmentPorting\deps\epicfight-1.21.1\src\main\java\yesman\epicfight\client\events\engine\ControlEngine.java
P:\FragmentPorting\deps\epicfight-1.21.1\src\main\java\yesman\epicfight\api\client\input\InputManager.java
P:\FragmentPorting\deps\epicfight-1.21.1\src\main\java\yesman\epicfight\client\input\DiscreteInputActionTrigger.java
```

Дополнительно после сборки есть распакованный кусок Epic Fight source/runtime рядом с портом:

```text
P:\FragmentPorting\nightfall-port\build\tmp\epicfight-src
```

Это build artifact, не источник истины, но удобен для быстрого сравнения с фактически подключенной зависимостью.

Avalon:

```text
P:\FragmentPorting\deps\avalon-1.20.1
P:\FragmentPorting\deps\avalon-1.21.1
```

Invincible:

```text
P:\FragmentPorting\deps\invincible-1.20.1
P:\FragmentPorting\deps\invincible-1.21.1
```

Важные классы Invincible для input/combos:

```text
P:\FragmentPorting\deps\invincible-1.21.1\src\main\java\com\p1nero\invincible\client\InputManager.java
P:\FragmentPorting\deps\invincible-1.21.1\src\main\java\com\p1nero\invincible\skill\ComboBasicAttack.java
P:\FragmentPorting\deps\invincible-1.21.1\src\main\java\com\p1nero\invincible\skill\SimpleCustomInnateSkill.java
```

## Runtime jars

В тестовом instance `test voxy` сейчас лежат:

```text
P:\PrismLauncher\instances\test voxy\minecraft\mods\epic-fight-21.17.3.1-mc1.21.1-neoforge.jar
P:\PrismLauncher\instances\test voxy\minecraft\mods\epic_fight_avalon-neoforge1.21.1-21.12.6.3.jar
P:\PrismLauncher\instances\test voxy\minecraft\mods\invincible-21.15.8.1-mc1.21.1-neoforge.jar
```

Локальные jar-библиотеки порта:

```text
P:\FragmentPorting\nightfall-port\libs\aaa_particles-neoforge-1.21.1-2.2.0.jar
P:\FragmentPorting\nightfall-port\libs\epic_fight_avalon-neoforge1.21.1-21.12.6.3.jar
P:\FragmentPorting\nightfall-port\libs\guhaos-vix-1531111-8258354.jar
P:\FragmentPorting\nightfall-port\libs\invincible-21.15.8.1-mc1.21.1-neoforge.jar
P:\FragmentPorting\nightfall-port\libs\smartkeyprompts-neoforge-1.21.1-1.1.3.jar
```

Версия Epic Fight в порте:

```text
P:\FragmentPorting\nightfall-port\gradle.properties
epicfight_version=21.17.3.1-mc1.21.1-neoforge
```

Важно: исходники Invincible 1.21.1 внутри своего `gradle.properties` указывают `epicfight_version=21.15.3-mc1.21.1-neoforge`, а runtime instance и порт используют Epic Fight 21.17.3.1. При следующем расследовании input это расхождение надо держать в голове.

## Build and install

Обычная сборка:

```powershell
Set-Location 'P:\FragmentPorting\nightfall-port'
.\gradlew build
```

Чистая сборка:

```powershell
Set-Location 'P:\FragmentPorting\nightfall-port'
.\gradlew clean build
```

Копирование jar в `test voxy`:

```powershell
$src = 'P:\FragmentPorting\nightfall-port\build\libs\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar'
$dst = 'P:\PrismLauncher\instances\test voxy\minecraft\mods\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar'
Copy-Item -LiteralPath $src -Destination $dst -Force
```

Проверка, что установлен именно новый jar:

```powershell
$src = 'P:\FragmentPorting\nightfall-port\build\libs\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar'
$dst = 'P:\PrismLauncher\instances\test voxy\minecraft\mods\efn-neoforge1.21.1-3.4.0-neoforge1.21.1.jar'
Get-FileHash -Algorithm SHA256 -LiteralPath $src, $dst
Get-Item -LiteralPath $src, $dst | Select-Object FullName, Length, LastWriteTime
```

Логи Minecraft:

```text
P:\PrismLauncher\instances\test voxy\minecraft\logs\latest.log
P:\PrismLauncher\instances\test voxy\minecraft\crash-reports
```

Быстрая проверка ошибок:

```powershell
Select-String -Path 'P:\PrismLauncher\instances\test voxy\minecraft\logs\latest.log' -Pattern '\[ERROR\]|\[FATAL\]|Game crashed|Crash|Exception|InvalidInjection|Critical injection failure|Cannot invoke|ClassCastException|NullPointerException' | Select-Object -Last 300
```

## Что не делать снова

Не чинить обычную атаку Epic Fight через широкий перехват `InputEvent.MouseButton.Pre`, ручной вызов приватных методов `ControlEngine`, миксины в `DiscreteInputActionTrigger` или искусственную отправку `SkillCastEvent` из EFN. Эта линия уже привела к тому, что Nightfall и обычный Epic Fight начали ломать друг друга, а mouse button 4 вел себя иначе, чем клавиши клавиатуры.

## Какие input-хуки остались

В проекте все еще есть старые input-mixin из initial port. Они не удалены этим cleanup-коммитом, потому что могут быть нужны Nightfall-комбо поверх Invincible:

```text
src/main/java/com/hm/efn/mixin/MixinInputManager.java
src/main/java/com/hm/efn/mixin/MixinKeyMapping.java
src/main/java/com/hm/efn/mixin/MixinKeyboardHandler.java
src/main/java/com/hm/efn/mixin/MixinMouseHandler.java
src/main/java/com/hm/efn/mixin/MouseHandlerMixin.java
src/main/java/com/hm/efn/client/input/LongPressKeyHandler.java
```

Особенно подозрителен `MixinInputManager`: он отменяет части `com.p1nero.invincible.client.InputManager` для weapon innate, если skill/item используют `ComboBasicAttack`. Это уже не поздний LКM-fallback, но именно его надо проверять следующим, если обычный Epic Fight или Nightfall input снова конфликтуют.

Следующая попытка должна идти от источников:

1. Сравнить `ControlEngine.maybeAttack` и `handleSeparateWeaponInnateSkill` в Epic Fight 1.21.1.
2. Сравнить `com.p1nero.invincible.client.InputManager` с тем, как EFN ожидает combo packets.
3. Проверить, не конфликтуют ли бинды `EpicFightInputAction.ATTACK`, `EpicFightInputAction.WEAPON_INNATE_SKILL`, vanilla `keyAttack` и Invincible input packets.
4. Проверить разницу поведения мышиных кнопок и клавиатуры в Invincible `onVanillaMouseOrKeyInput`.
5. Если нужен patch, делать его минимально и желательно в одном месте, где реально находится несовместимость, а не на входе всех mouse events.

## Известные рабочие области

Модели оружия после предыдущих фиксов отображались корректно в руках.

Трейлы были видны.

Краш на обычный ЛКМ после части VFX/input правок уходил, но дальше выяснилось, что обычный Epic Fight attack не работает корректно, а вместо обычной атаки часто уходит charged/weapon innate путь. Поэтому input-правки очищены.

Часть VFX была стабилизирована, включая lifetime для проблемных эффектов. Коммит `2a2e023 Guard AAA particle cleanup during world load` оставлен.

Предметы/броня должны иметь ограничение stack size через текущие item classes. Это надо перепроверить в игре после очистки input.

## Следующие точки расследования

Проверить в игре на чистом baseline:

```text
1. Survival: обычное оружие Epic Fight, ЛКМ, короткое нажатие.
2. Survival: обычное оружие Epic Fight, удержание ЛКМ.
3. Survival: Nightfall weapon, короткое ЛКМ.
4. Survival: Nightfall weapon, weapon innate на клавиатуре.
5. Survival: Nightfall weapon, weapon innate на mouse button 4.
6. Creative: те же пункты, только для сравнения.
```

Если survival и creative расходятся, смотреть не "креатив маскирует", а конкретно условия:

```text
LocalPlayerPatch.canPlayAttackAnimation()
SkillContainer.canUse(...)
ComboBasicAttack.checkExecuteCondition(...)
player.getAbilities().instabuild
cooldown/attack strength
current holding action in ControlEngine
```

Для VFX:

```text
src/main/java/com/guhao/vix/particles
src/main/java/com/hm/efn/client/effek
src/main/java/com/hm/efn/client/particle
src/main/java/com/hm/efn/entity/effect
```

Для item stack size:

```text
src/main/java/com/hm/efn/item
src/main/java/com/hm/efn/item/custom
src/main/java/com/hm/efn/item/geo
```

Для capabilities and movesets:

```text
src/main/java/com/hm/efn/gameasset/EFNWeaponCapabilityPresets.java
src/main/java/com/hm/efn/gameasset/EFNSkills.java
src/main/java/com/hm/efn/gameasset/combos
src/main/resources/data/efn/epicfight
```

## Полезные команды поиска

Проверить, что временные input-хуки не вернулись:

```powershell
rg -n "VanillaAttackInputFallback|EFNBasicAttackRouting|EFNInputKeyUtil|ControlEngineInvoker|EpicFight.*Input|ComboAttacksTrace|EFN/InputTrace" src/main/java src/main/resources/efn.mixins.json
```

Найти static animation caches:

```powershell
rg -n "private static final AnimationAccessor|static final AnimationAccessor|= EFN.*Animations" src/main/java/com/hm/efn/skill
```

Найти ручные input bridges:

```powershell
rg -n "InputEvent.MouseButton|InputEvent.Key|sendCastRequest|SkillCastEvent|reserveKey|consumeClick|isLeftPressed|isBoundToSamePhysicalInput" src/main/java
```

Проверить mixin config:

```powershell
Get-Content -LiteralPath 'P:\FragmentPorting\nightfall-port\src\main\resources\efn.mixins.json'
```

## Git

Перед пушем:

```powershell
git status --short --branch
git diff --cached --stat
.\gradlew build
```

Пуш:

```powershell
git push origin dev-0.0.1
```
