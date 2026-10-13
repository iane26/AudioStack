# AudioStack - Deliverable 3: Prototype + Tests

CS 2114 - Software Design & Data Structures - Fall 2026 - Project 2
Team: Luci, Emma, Ian, Sajjad

AudioStack is a console playlist manager for people who listen to both music and podcasts. A user builds ordered playlists of songs and podcast episodes and can add, insert at a position, view, remove, and count items. They can also keep several playlists and see a log of recent activity. The provided `LinkedChain<T>` does all of the storage, **used exactly as provided, with no changes**.

---

## How to compile and run

`src/` holds the program and `test/` holds the JUnit 5 tests. All classes are in the default package, so the provided `LinkedChain.java` is byte-for-byte the file we were given; not even a `package` line was added.

### In Eclipse

1. **File -> Import -> General -> Existing Projects into Workspace -> Next.**
2. For **Select root directory**, browse to this folder (the one containing `src` and `test`). Then click **Finish**.
3. **Run the program:** right-click `src/AudioStackApp.java` -> **Run As -> Java Application**.
4. **Run the tests:** right-click the `test` folder -> **Run As -> JUnit Test**. You should get a green bar with **99/99** passing.

If the test files show red `org.junit` errors, right-click the project -> **Build Path -> Add Libraries... -> JUnit -> JUnit 5 -> Finish**.

### From a terminal (Windows, macOS, or Linux)

You need Java 11 or newer. Run these from this folder:

```
javac -d out -cp lib/junit-platform-console-standalone-1.9.1.jar src/*.java test/*.java

java -cp out AudioStackApp

java -jar lib/junit-platform-console-standalone-1.9.1.jar execute --class-path out --scan-class-path
```

The first line compiles. The second runs the program; type `0` to quit. The third runs the tests and should end with `99 tests successful, 0 tests failed`.

**Why JUnit 5 and not `student.TestCase`:** earlier this semester, `student.TestCase` crashed on the Java 21 lab machines before any test ran (`The Security Manager is deprecated`). A test file that doesn't run counts as not present, so we used plain JUnit 5. It's built into Eclipse and runs on any Java version. `lib/` holds the standalone JUnit runner so the tests also run without Eclipse.

---

## What's in the prototype

| Class | Job |
|---|---|
| `LinkedChain<T>` | Provided generic storage, **unchanged**. |
| `MediaItem` (abstract) | Title and creator shared by every media type, plus the one equality rule. |
| `Song`, `Podcast` | A music track (artist, optional album) and a podcast episode (show, episode number). |
| `Playlist` | One named playlist. Owns the 1-based position rules, null checks, counting, and the numbered display. |
| `Library` | All of the user's playlists, plus which one is being edited. *(Stretch goal from our scope: multiple playlists)* |
| `History` | A newest-first log of what the user did this session. |
| `InputHelper` | Reads keyboard lines and re-prompts until it gets a whole number or non-blank text. |
| `PlaylistMenu` | The menu loop. Builds items from input, calls `Playlist`/`Library`, and prints results and errors. |
| `AudioStackApp` | `main`: wires everything together and starts the menu. |

Calls flow one way: `PlaylistMenu` -> `Playlist` / `Library` / `History` -> `LinkedChain`. No app class touches a node, and the chain holds no application logic.

### One LinkedChain, three element types

| Element type | Where | Provided methods it calls |
|---|---|---|
| `MediaItem` (`Song` and `Podcast` objects) | `Playlist.items` | `add`, `addToEnd`, `remove()`, `remove(T)`, `get`, `count`, `clear`, `size`, `isEmpty`, `toArray` |
| `Playlist` | `Library.playlists` | `addToEnd`, `get`, `size`, `toArray` |
| `String` | `History.entries` | `add` (front, so newest first), `isEmpty`, `size`, `toArray` |

All three go through the same `LinkedChain<T>` class, with no copies and no casts.

### Menu

```
 1. Add a song or podcast      6. Remove an item
 2. Insert at a position       7. Count an item
 3. View the playlist          8. New playlist        (stretch: multiple playlists)
 4. View one item              9. Switch playlist     (stretch: multiple playlists)
 5. Remove by position        10. Recent activity
                               0. Quit
```

---

## Bad input: where it's caught and what the user sees

`PlaylistMenuTest.testRunBadInputsFromScope` types every keyboard bad input from our scope in one session. It checks that each message prints once per bad input, nothing crashes, and rejected actions leave the playlist unchanged. Null objects can't come from the keyboard, so the unit tests in the last column cover those. They also check every other case on its own.

| Bad input | Caught in (as spec Section 6 says) | Response | Unit test |
|---|---|---|---|
| Non-number for an option, position, or episode | `InputHelper.readInt` | Re-prompt: `Please enter a whole number.` | `InputHelperTest.testReadIntBad` |
| Blank title, artist, or show | `InputHelper.readNonBlank` | Re-prompt: `This cannot be blank.` | `InputHelperTest.testReadNonBlankBad` |
| Repeated invalid input | `InputHelper` loops | The same message every time, until valid | `testReadIntBad` (three bad lines in a row) |
| Blank or null title/creator reaching the constructor | `MediaItem` constructor | Exception: `Title cannot be blank.` / `Creator cannot be blank.` | `SongTest.testConstructorBad` |
| Blank or null playlist name | `Playlist` constructor | Exception: `Playlist name cannot be blank.` | `PlaylistTest.testConstructorBad` |
| Null album | `Song` constructor | Default: stored as `""` | `SongTest.testGetAlbumBadNull` |
| Null song or podcast added or inserted | `Playlist.addItem` / `insertItem` | Exception: `Item cannot be null.` | `PlaylistTest.testAddItemBadNull` |
| Null item passed to remove or count | `Playlist.removeItem` / `countItem` | Exception: `Item cannot be null.` | `PlaylistTest.testRemoveItemBadNull` |
| Insert position below 1 or above size + 1 | `Playlist.insertItem` | Exception: `Position must be between 1 and N.`, playlist unchanged | `PlaylistTest.testInsertItemBad` |
| View or remove position below 1 or above size | `Playlist.getItem` / `removeItemAt` | Exception: `Position must be between 1 and N.` | `PlaylistTest.testGetItemBad`, `testRemoveItemAtBad` |
| Remove from an empty playlist | `PlaylistMenu` checks `isEmpty()` first; `Playlist.removeItemAt` throws as a backstop | `Nothing to remove.` | `PlaylistTest.testRemoveItemAtBad` |
| Remove an item that isn't in the playlist | `Playlist.removeItem` returns `false` | `Not in the playlist.` | `PlaylistTest.testRemoveItemBadNotThere` |
| Count an item that isn't in the playlist | `Playlist.countItem` returns 0 | `Count: 0` | `PlaylistTest.testCountItemBad` |
| Episode number below 1 | `Podcast` constructor, caught in `PlaylistMenu.run` | `Episode number must be at least 1.` | `PodcastTest.testConstructorBad` |
| Unknown menu option | `PlaylistMenu.run` default case | `Unknown option.` | `testRunBadInputsFromScope` |
| End of input (Ctrl-D / Ctrl-Z) | `PlaylistMenu.run` catches `NoSuchElementException` | `Goodbye.` with no stack trace | `PlaylistMenuTest.testRunBadEndOfInput` |
| Null Scanner or null collaborator | `InputHelper` / `PlaylistMenu` constructors | Exception: `Scanner cannot be null.` / `Library and input cannot be null.` | `InputHelperTest.testConstructorBadNull`, `PlaylistMenuTest.testConstructorBadNull` |

---

## Big-O of the app methods vs. the spec

n = items in the current playlist, p = playlists, h = history entries, k = input attempts, a = menu actions.

| Method | Spec said | Actual | Match? |
|---|---|---|---|
| `Playlist(name)`, `getName`, `size`, `isEmpty` | O(1) | O(1) | Yes |
| `Playlist.addItem` | O(1) | **O(n)** | **No.** The spec planned a `lastNode` tail reference. We used the provided chain unchanged, and its `addToEnd` walks to the end. |
| `Playlist.insertItem` | O(n) | O(n); O(1) at position 1 | Yes. Position 1 uses the provided O(1) front `add`. Other positions copy out with `toArray` (O(n)), `clear` (O(1)), and refill with n front `add`s (O(n)). |
| `Playlist.getItem` | O(n) | O(n) | Yes, via the provided `get` |
| `Playlist.removeItemAt` | O(n) | O(n); O(1) at position 1 | Yes. Position 1 uses the provided O(1) `remove()`; other positions copy out and refill, like insert. |
| `Playlist.removeItem` | O(n) | O(n), the provided `remove(T)` | Yes |
| `Playlist.countItem` | O(n) | O(n), the provided `count` | Yes |
| `Playlist.toDisplayString` | O(n) | O(n), one pass over `toArray` | Yes |
| `MediaItem`, `Song`, `Podcast`: all methods | O(1) | O(1) (string length treated as constant) | Yes |
| `InputHelper(Scanner)`, `readLine` | O(1) | O(1) | Yes |
| `InputHelper.readInt`, `readNonBlank` | O(k) | O(k) | Yes |
| `PlaylistMenu(library, input)` | O(1) | O(1) | Yes |
| `PlaylistMenu.run`, `AudioStackApp.main` | O(a * n) | O(a * (n + p + h)) | Yes, for the spec's options. The new switch and history options add the p and h terms. |
| `Library(first)`, `getCurrent`, `size` | not in spec | O(1) | New |
| `Library.addPlaylist` | not in spec | O(p), the provided `addToEnd` | New |
| `Library.switchTo`, `toDisplayString` | not in spec | O(p) | New |
| `History()`, `History.record` | not in spec | O(1), the provided front `add` | New |
| `History.toDisplayString` | not in spec | O(h) | New |

---

## Reflection

### What changed from our specification?

Each change lists where it came from.

1. **We used the provided `LinkedChain` unchanged.** Section 5 of our spec planned to add `lastNode`, `insert(int, T)`, `remove(int)`, `removeEntry(T)`, and null rejection. Once we had the provided file, it already had `get(int)` and null rejection. Its `remove(T)`, `count(T)`, and `size()` cover what we planned to call `removeEntry`, `getFrequencyOf`, and `getCurrentSize`. The only things missing were insert-at and remove-at an index, and `Playlist` can do those through the public methods (change 2). So we left the class exactly as given. *Origin: group decision after reading the provided file.*
2. **`Playlist` does index work through the chain's public methods.** Position 1 uses the chain's O(1) front `add()` and `remove()`. Any other position copies the items out with `toArray`, then `clear`s the chain and refills it with front `add()`, going from the last item to the first so the order comes out right. This is still O(n), the Big-O our spec promised, and the rule stays in `Playlist` where the spec put it. *Origin: GenAI (Claude) suggested the refill approach; the group chose it over modifying the chain.*
3. **`addItem` is O(n), not O(1).** Without our planned `lastNode`, the provided `addToEnd` walks the chain. See the Big-O table. *Origin: consequence of change 1.*
4. **No casts, even from `toArray`.** The spec planned an `Object[] toArray()`. The provided `toArray(T[])` fills an array we pass in, so `Playlist` gets a real `MediaItem[]`. *Origin: provided class.*
5. **Two new classes, `Library` and `History`.** `Library` is the multiple-playlists stretch goal from our scope, storing a `LinkedChain<Playlist>` as our revised scope planned. `History` stores a `LinkedChain<String>` activity log. Together with `MediaItem`, three different element types go through one chain. Because of this, the `PlaylistMenu` constructor takes a `Library` instead of a `Playlist`, and its null message is now `Library and input cannot be null.` *Origin: GenAI review of the rubric; multiple playlists came from our scope.*
6. **Menu options 0-10 instead of 0-7.** The three new options are new playlist, switch playlist, and recent activity. Our spec's run() test used option 9 as the unknown option; 9 is now valid, so the test uses 99. *Origin: follows from change 5.*
7. **Viewing one item on an empty playlist** now prints `The playlist is empty.` before asking for a position, instead of `Position must be between 1 and 0.` A direct call to `getItem` still throws the spec's IAE. *Origin: own reflection while testing.*
8. **A media type other than 1 or 2 re-prompts** with `Please enter 1 or 2.` The spec didn't list this case. *Origin: GenAI, while building `readMediaItem`.*
9. **`main` starts with a playlist named "My Playlist"** instead of asking for one, so the spec's main() test ("no arguments, the menu appears") holds. Users can name new playlists with option 8. *Origin: GenAI.*
10. **Tests cover the app classes, not `LinkedChain`.** Our test plan included `LinkedChain` tests for the methods we planned to add. We didn't add them, and the rubric asks for tests per public method of our app classes. Since `Playlist` now does the index work, `PlaylistTest` checks insert and remove at the first, middle, and last positions. *Origin: follows from change 1.*
11. **JUnit 5 instead of `student.TestCase`.** See "How to compile and run." *Origin: our experience with the lab machines earlier this semester.*

### What changed in the LinkedChain?

Nothing. We used `LinkedChain.java` unchanged, and it is byte-for-byte the provided file. Our app needed to insert and remove at a position. The chain has no methods for that, so `Playlist` builds them from `toArray`, `clear`, `add`, and `remove()`. The tradeoff is that appending stays O(n) instead of the O(1) our spec planned.

### What happened to our unknowns?

These are the open questions from our scope's "What you don't know" section:

- **How to structure Songs and Podcasts:** solved. `MediaItem` holds the title and creator; `Song` adds an album and `Podcast` adds an episode number.
- **How `equals()` should work:** solved. Items match on type, title, and creator, ignoring case, and podcasts also need the same episode number. `SongTest.testEqualsNormalIgnoresCase` and `PodcastTest.testEqualsBad` confirm it.
- **How to handle repeated invalid Scanner input:** solved. `InputHelper` reads with `nextLine()` and parses with `Integer.parseInt`. `testReadIntBad` sends three bad lines in a row and gets the same message each time.
- **Head/tail edge cases in the chain:** this was a risk while we planned to modify the chain. Using it unchanged removed the risk. The equivalent risk moved to `Playlist`'s refill, which `PlaylistTest.testInsertItemEdgeMiddleAndEmpty` and `testRemoveItemAtEdgeFirstAndMiddle` cover. As a check, we deliberately reversed the refill order, and three tests failed.
- **Still open:** whether our TA wants `student.TestCase` for the final.

### What doesn't work yet?

Every MVP feature works, and all 99 tests pass. What's missing:

- **Move/reorder** (a stretch goal) isn't built yet.
- **Shuffle** (a stretch goal) isn't built.
- **Appending is O(n)** rather than the spec's O(1), as described above.
- **The em dash (—) in item listings** shows as `?` in some plain terminals, such as Windows Command Prompt. It displays correctly in Eclipse.

---

## Next Steps

Ordered by when we'll do them. *(Owners are proposed from who worked on each area in Deliverable 2; confirm before submitting.)*

The MVP is complete, so nothing new is needed to finish it. These are the remaining fixes and additions, in order:

| # | Task | Owner | New tests it needs |
|---|---|---|---|
| 1 | Everyone reads the code, runs the 99 tests on their own machine, and fills in the attribution table | All four | None |
| 2 | Ask our TA whether JUnit 5 is fine for the final or `student.TestCase` is required; convert if needed | Emma | The same tests in JUnit 3 style, if required |
| 3 | **Move/reorder (committed).** Add `Playlist.moveItem(int from, int to)` using `removeItemAt` then `insertItem`, and add a menu option. | Ian | `testMoveItemNormal` (to the front and to the end), `testMoveItemEdgeSamePosition` (no change), `testMoveItemBad` (position 0 or size + 1, playlist unchanged), `testMoveItemBadEmpty`, and a menu test `testRunMove` |
| 4 | Update the system diagram and Big-O for `Library`, `History`, and the unchanged chain | Luci | None |
| 5 | Final check: every test passes on a lab machine and the README is current | Sajjad | None |

**Stretch goals from our scope:**

| Stretch goal | Decision |
|---|---|
| Multiple playlists | **Done**: `Library`, menu options 8-9 |
| Move/reorder | **Committed**: Next Steps #3 |
| Shuffle | **Dropped**: random output is hard to test reliably, and move gives more value for the time we have |

---

## Attribution

**Fill in the "Reviewed / tested by" column before submitting.** Each person should list only what they actually did.

| File | Designed in our spec by (D2) | How the code was made | Reviewed / tested by |
|---|---|---|---|
| `LinkedChain.java` | (provided) | Provided by CS 2114 staff; used unchanged | |
| `MediaItem.java` | Luci (class design), Ian (signatures) | GenAI-written (Claude) from our spec | |
| `Song.java` | Luci, Ian | GenAI-written (Claude) from our spec | |
| `Podcast.java` | Luci, Ian | GenAI-written (Claude) from our spec | |
| `Playlist.java` | Luci, Ian, Emma | GenAI-written (Claude) from our spec | |
| `Library.java` | Scope stretch goal (multiple playlists) | GenAI-written (Claude) | |
| `History.java` | Not in spec | GenAI-written (Claude) | |
| `InputHelper.java` | Ian, Sajjad (validation) | GenAI-written (Claude) from our spec | |
| `PlaylistMenu.java` | Luci, Sajjad | GenAI-written (Claude) from our spec | |
| `AudioStackApp.java` | Luci | GenAI-written (Claude) from our spec | |
| `SongTest.java`, `PodcastTest.java` | Sajjad, Ian (test plan) | GenAI-written (Claude) from our test plan | |
| `PlaylistTest.java` | Sajjad, Ian | GenAI-written (Claude) from our test plan | |
| `LibraryTest.java`, `HistoryTest.java` | Not in spec | GenAI-written (Claude) | |
| `InputHelperTest.java` | Sajjad, Ian | GenAI-written (Claude) from our test plan | |
| `PlaylistMenuTest.java`, `AudioStackAppTest.java` | Sajjad, Ian | GenAI-written (Claude) from our test plan | |
| `README.md` | All | GenAI-drafted (Claude); team to review | |

### How we used GenAI
Claude helped us build a small starting Java prototype from our Deliverable 2 specification, which we then fit to the staff-provided LinkedChain. It also drafted our Javadocs, wrote JUnit test cases, and polished the phrasing in our documentation. When our tests caught failures in the playlist display and search comparisons, we used them to direct our fixes. The design itself comes from our team’s specification and the instructor’s reusable chain; no new data structure was generated for this assignment. At the end, we used Claude to clean up the code’s formatting, solve issues we couldn’t fix on our own, and shorten the README.

