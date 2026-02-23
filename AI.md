Cursor, Codex

observations: in level 3 when implementing mark as done, the AI suggested to handle errors too, which was unnecessary as that would have been for level 5

in level 4, AI suggested hardcoded .substring(9) as compared to using the string itself "deadline"

opus refactored out the code to different function, while composite did not

level 6 enum - asked AI if it's good to have enum, AI said yes and refactored many files, but at this current point, a boolean done/not done is sufficient and better than making enum changes,
so it's up to the programmer to decide the tradeoff and the AI might not always be correct.

level 7 - ai suggested Optional<Task> maybeTask = parseLine(line);
maybeTask.ifPresent(tasks::add);

but it could have been just chained parseLine(line).ifPresent(tasks::add);

List<String> lines = tasks.stream()
                .map(this::serialize)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
                 
                according to AI is 
                could be just 
List<String> lines = tasks.stream()
                .map(this::serialize)
                .filter(Objects::nonNull)
                .toList();  // ✅ More concise! for Java 16+

level 8 - ai accepts ISO format, should only allow human readable format for a chatbot
- AI used repeated try/catch blocks, i asked to refactor into multi format parsing into a loop over formatters

docs-learnt something new, @inheritdocs

level9 - Locale.ROOT

A-CheckStyle, agent fix much faster than me, using codex

level 10 - generate kraken and user image, follow guide part by part
